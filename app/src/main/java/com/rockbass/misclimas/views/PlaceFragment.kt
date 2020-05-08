package com.rockbass.misclimas.views

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.rockbass.misclimas.MAPBOX_TOKEN
import com.rockbass.misclimas.R
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.helpers.colocarIdCiudad
import com.rockbass.misclimas.viewmodels.PlaceViewModel

class PlaceFragment : Fragment() {

    private val REQUEST_CODE_AUTOCOMPLETE = 10
    private var ciudad: Ciudad? = null
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var mapView : MapView
    private var mapboxMap: MapboxMap? = null
    private val symbolIconId = "symbolIconId"
    private val geojsonSourceLayerId = "geojsonSourceLayerId"
    private lateinit var buttonEscoger : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placeViewModel = defaultViewModelProviderFactory.create(PlaceViewModel::class.java)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_CODE_AUTOCOMPLETE){
                val feature = PlaceAutocomplete.getPlace(data)
                feature?.also { carmenFeature ->
                    locateMap(carmenFeature)
                }
            }
        }
    }

    fun locateMap(carmenFeature: CarmenFeature){
        val point = carmenFeature.center()
        ciudad = Ciudad(
            name = carmenFeature.placeName(),
            longitude = point?.longitude(),
            latitude = point?.latitude(),
            altitude = point?.altitude()
        )

        mapboxMap?.let {mapboxMap ->
            val style = mapboxMap.style
            style?.let {style ->
                val source = style
                    .getSourceAs<GeoJsonSource>(geojsonSourceLayerId)
                source?.setGeoJson(
                    FeatureCollection.fromFeatures(
                        listOf(
                            Feature.fromJson(carmenFeature.toJson())
                        )
                    )
                )
            }

            mapboxMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(
                            LatLng(
                                carmenFeature.center()!!.latitude(),
                                carmenFeature.center()!!.longitude()
                            )
                        )
                        .zoom(12.0)
                        .build()
                ), 3000
            )
        }

        buttonEscoger.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.place_fragment, container, false)

        mapView = view.findViewById(R.id.mapview)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(mapConfiguration)

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.fab_location_search)
        floatingButton.setOnClickListener(clickFab)

        buttonEscoger = view.findViewById(R.id.button_escoger)
        buttonEscoger.setOnClickListener(clickEscoger)

        return view
    }

    private val clickEscoger = View.OnClickListener {
        ciudad?.let {ciudad->
            placeViewModel.insertarCiudad(ciudad).observe(
                viewLifecycleOwner, Observer { idCiudad ->
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
            .accessToken(MAPBOX_TOKEN)
            .placeOptions(placeOptions)
            .build(activity)
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
    }

    private val mapConfiguration = object : OnMapReadyCallback{
        override fun onMapReady(mapboxMap: MapboxMap) {
            this@PlaceFragment.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS, Style.OnStyleLoaded { style ->
                style.addImage(
                    symbolIconId,
                    resources.getDrawable(R.drawable.ic_location_on_black_24dp, null)
                )

                setUpSource(style)

                setupLayer(style)
            })
        }

        fun setUpSource(loadedMapStyles: Style){
            loadedMapStyles.addSource(
                GeoJsonSource(
                    geojsonSourceLayerId
                )
            )
        }

        private fun setupLayer(loadedMapStyle: Style) {
            loadedMapStyle.addLayer(
                SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                    iconImage(symbolIconId),
                    iconOffset(arrayOf(0f, -8f))
                )
            )
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

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}