package com.rockbass.misclimas.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.result.SearchResult
import com.rockbass.misclimas.BuildConfig
import com.rockbass.misclimas.R
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.helpers.colocarIdCiudad
import com.rockbass.misclimas.helpers.isDarkThemeOn
import com.rockbass.misclimas.viewmodels.PlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import android.view.WindowInsets.Type.*

private const val REQUEST_LOCATION_AND_PHONE = 0
private const val REQUEST_CODE_AUTOCOMPLETE = 10
private const val symbolIconId = "symbolIconId"

@AndroidEntryPoint
class PlaceFragment : Fragment() {

    private var permissionLocalization = false
    private var permissionReadPhone = false
    private var ciudad: Ciudad? = null
    private val placeViewModel: PlaceViewModel by viewModels()
    private lateinit var mapView : MapView
    private var mapboxMap: MapboxMap? = null
    private lateinit var buttonSetLocation : MaterialButton
    private lateinit var fabLocation : FloatingActionButton
    private lateinit var fabCurrentLocation: FloatingActionButton
    private val reverseGeoCodingSearch = MapboxSearchSdk.createReverseGeocodingSearchEngine()
    private lateinit var symbolManager: SymbolManager

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CODE_AUTOCOMPLETE){
                val feature = PlaceAutocomplete.getPlace(data)
                feature?.also { carmenFeature ->
                    if (carmenFeature.center() != null && carmenFeature.placeName()!= null)
                        locateMap(carmenFeature.center()!!, carmenFeature.placeName()!!)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_AND_PHONE ) {
            val indexPermissionLocation = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if (indexPermissionLocation != -1) {
                permissionLocalization = grantResults[indexPermissionLocation] == PackageManager.PERMISSION_GRANTED
            }

            val indexPermissionReadPhone = permissions.indexOf(Manifest.permission.READ_PHONE_STATE)
            if (indexPermissionReadPhone != -1) {
                permissionReadPhone = grantResults[indexPermissionReadPhone] == PackageManager.PERMISSION_GRANTED
            }

            if (!showExplanations()) {
                if (!permissionLocalization) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("No podra utilizar los servicios de localización del movil")
                        .show()
                }

                if (!permissionReadPhone) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("No podra utilizar los servicios de lectura del telefono, el mapa podría funcionar incorrectamente")
                        .show()
                }
            }

            configMap()
        }
    }

    private fun locateMap(point: Point, cityName: String){
        ciudad = Ciudad(
            name = cityName,
            longitude = point.longitude(),
            latitude = point.latitude(),
            altitude = point.altitude()
        )

        symbolManager.annotations.clear()

        val symbolOptions = SymbolOptions()
            .withIconImage(symbolIconId)
            .withIconOffset(arrayOf(0f, -8f))
            .withTextOffset(arrayOf(0f, 3f))
            .withTextField(cityName)
            .withGeometry(point)

        symbolManager.create(symbolOptions)

        mapboxMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(
                        LatLng(
                            point.latitude(),
                            point.longitude()
                        )
                    )
                    .zoom(12.0)
                    .build()
            ), 3000
        )

        buttonSetLocation.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.place_fragment, container, false)

        mapView = view.findViewById(R.id.mapview)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (needPermission() || !showExplanations())) {
            requestPermissions()
        } else {
            configMap(savedInstanceState)
        }

        fabLocation = view.findViewById(R.id.fab_location_search)
        fabLocation.setOnClickListener(clickFab)

        buttonSetLocation = view.findViewById(R.id.button_escoger)
        buttonSetLocation.setOnClickListener(clickEscoger)

        fabCurrentLocation = view.findViewById(R.id.fabLocationOn)
        fabCurrentLocation.setOnClickListener(fabCurrentLocationOnClick)

        return view
    }

    private fun showExplanations() : Boolean {
        var showed = false

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showed = true

            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Los permisos de localización son necesarios en caso de que quiera ubicar su posición")
                .setPositiveButton("Permitir") { _, _ ->
                    requestPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                .setNegativeButton("No Gracias", null)
                .show()
        }else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            showed = true

            MaterialAlertDialogBuilder(requireContext())
                .setMessage("El permiso de llamadas es necesario para saber el estado actual del telefono y hacer que el mapa funcione correctamente")
                .setPositiveButton("Permitir") { _, _ ->
                    requestPermissions(
                        Manifest.permission.READ_PHONE_STATE
                    )
                }
                .setNegativeButton("No Gracias", null)
                .show()
        }

        return showed
    }

    private fun requestPermissions(vararg permissions: String = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_PHONE_STATE
    )) {
        requestPermissions(
            permissions,
            REQUEST_LOCATION_AND_PHONE
        )
    }

    private fun needPermission(): Boolean {
        val locationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val readPhonePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)

        permissionLocalization = locationPermission == PackageManager.PERMISSION_GRANTED
        permissionReadPhone = readPhonePermission == PackageManager.PERMISSION_GRANTED

        return !permissionLocalization && !permissionReadPhone
    }

    private fun configMap(savedInstanceState: Bundle? = null) {
        if (permissionReadPhone)
            mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(mapConfiguration)
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (permissionLocalization) {
            val locationComponent = mapboxMap?.locationComponent

            val locationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .build()

            locationComponent?.activateLocationComponent(locationOptions)
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.renderMode = RenderMode.NORMAL

            fabCurrentLocation.visibility = View.VISIBLE
        }
    }

    private val clickEscoger = View.OnClickListener {
        ciudad?.let {ciudad->
            placeViewModel.insertarCiudad(ciudad).observe(
                viewLifecycleOwner, { idCiudad ->
                    //Redirigir a pantalla principal
                    activity?.colocarIdCiudad(idCiudad)

                    NavHostFragment.findNavController(this)
                        .navigate(
                            R.id.action_placeAutoCompleteFragment_to_indexFragment)
                }
            )
        }

    }

    private val clickFab = View.OnClickListener {
        val placeOptions = PlaceOptions
            .builder()
            .backgroundColor(Color.WHITE)
            .build()

        val intent = PlaceAutocomplete.IntentBuilder()
            .accessToken(BuildConfig.MapboxAcessToken)
            .placeOptions(placeOptions)
            .build(activity)
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
    }

    private val fabCurrentLocationOnClick = View.OnClickListener {
        val location = mapboxMap?.locationComponent?.lastKnownLocation
        if (location?.longitude != null || location?.latitude != null) {
            searchGeoCoding(location.longitude, location.latitude)
        }
    }

    private fun searchGeoCoding(longitude: Double, latitude: Double) {
        val point = Point.fromLngLat(
            longitude,
            latitude
        )

        val reverseOptions = ReverseGeoOptions.Builder(point)
            .limit(1)
            .build()

        reverseGeoCodingSearch.search(
            reverseOptions,
            object: SearchCallback {
                override fun onError(e: Exception) {
                    Timber.e(e)
                }

                override fun onResults(results: List<SearchResult>) {
                    if (results.isNotEmpty()) {
                        val result = results.first()
                        if (result.coordinate != null && result.address?.place != null)
                            locateMap(result.coordinate!!, result.address?.place!!)
                    } else {
                        Toast.makeText(requireContext(), "Datos de la ciudad no encontrados", Toast.LENGTH_LONG).show()
                    }
                }

            }
        )
    }

    private val mapConfiguration = OnMapReadyCallback { mapboxMap ->
        fabLocation.isEnabled = true
        this@PlaceFragment.mapboxMap = mapboxMap

        mapboxMap.addOnMapLongClickListener { position ->
            searchGeoCoding(position.longitude, position.latitude)
            true
        }

        val styleMap = if (requireContext().isDarkThemeOn()) Style.DARK else Style.LIGHT

        mapboxMap.setStyle(styleMap) { style ->
            enableLocationComponent(style)
            symbolManager = SymbolManager(mapView, mapboxMap, style)

            symbolManager.iconAllowOverlap = true

            style.addImage(
                symbolIconId,
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_location_on_black_24dp,
                    null
                )!!
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(
                navigationBars() or captionBar() or systemBars() or statusBars()
            )
        } else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }

    }

    override fun onDetach() {
        super.onDetach()
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.show(
                navigationBars() or captionBar() or systemBars() or statusBars()
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}