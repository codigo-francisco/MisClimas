package com.rockbass.misclimas.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.rockbass.misclimas.R
import com.rockbass.misclimas.viewmodels.LoadViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoadFragment : Fragment() {

    private val loadViewModel: LoadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.load_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()

        loadViewModel.cantidadCiudad()
            .observe(viewLifecycleOwner,
                {cantidad ->
                    val navController = NavHostFragment.findNavController(this)
                    if (cantidad > 0){
                        navController.navigate(R.id.action_loadFragment_to_indexFragment)
                    }else{
                        navController.navigate(R.id.action_loadFragment_to_explanationFragment)
                    }
                })

    }
}