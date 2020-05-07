package com.rockbass.misclimas.views

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rockbass.misclimas.*
import com.rockbass.misclimas.adapters.ClimaCardAdapter
import com.rockbass.misclimas.databinding.ClimaCardBinding
import com.rockbass.misclimas.viewmodels.IndexViewModel

class IndexFragment: Fragment() {

    private val TAG = IndexFragment::class.java.name
    private var idCiudad: Long? = null
    private val indexViewModel =
        defaultViewModelProviderFactory.create(IndexViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idCiudad = activity?.leerIdCiudad()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.index_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_climas)
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        recyclerView.setHasFixedSize(true)

        indexViewModel.getCiudad(idCiudad!!)
            .observe(viewLifecycleOwner,
            Observer {ciudad ->
                indexViewModel.getClima(
                    ciudad.latitude!!,
                    ciudad.longitude!!
                ).observe(
                    viewLifecycleOwner,
                    Observer { response ->
                        if (!response.hasError){
                            val data = response.data
                            val card = view.findViewById<View>(R.id.cardView_index)
                            val dataBinding = DataBindingUtil.bind<ClimaCardBinding>(card)
                            dataBinding?.clima = data?.first()
                            dataBinding?.executePendingBindings()

                            val restData = data?.drop(1)
                            val climaAdapter = ClimaCardAdapter(restData!!)
                            recyclerView.adapter = climaAdapter
                            climaAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                context,
                                R.string.error_solicitud_clima,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            })

        return view
    }

}