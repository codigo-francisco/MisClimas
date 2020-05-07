package com.rockbass.misclimas.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.rockbass.misclimas.R

class ExplanationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.primeravez_fragment, container, false)

        val buttonContinuar: MaterialButton = view.findViewById(R.id.button_continuar)
        buttonContinuar.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_explanationFragment_to_placeAutoCompleteFragment)
        }

        return view
    }
}