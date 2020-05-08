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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.rockbass.misclimas.*
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.viewmodels.PlaceViewModel

class PlaceFragment : Fragment() {

    private val REQUEST_CODE_AUTOCOMPLETE = 10
    private lateinit var placeViewModel: PlaceViewModel

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
                    val point = carmenFeature.center()
                    val ciudad = Ciudad(
                        name = feature.placeName(),
                        longitude = point?.longitude(),
                        latitude = point?.latitude(),
                        altitude = point?.altitude()
                    )
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
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.placeautocomplete_fragment, container, false)

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.fab_location_search)
        floatingButton.setOnClickListener {
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

        return view
    }

}