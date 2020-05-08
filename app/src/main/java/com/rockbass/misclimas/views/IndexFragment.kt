package com.rockbass.misclimas.views

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rockbass.misclimas.*
import com.rockbass.misclimas.adapters.ClimaCardAdapter
import com.rockbass.misclimas.databinding.ClimaCardBinding
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.viewmodels.IndexViewModel

class IndexFragment: Fragment() {

    private var idCiudad: Long? = null
    private lateinit var indexViewModel: IndexViewModel
    private var loadData = false

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_add -> agregar()
            R.id.menu_delete -> eliminar()
        }

        return super.onOptionsItemSelected(item)
    }

    fun agregar() {
        //Navegamos para agregar ciudades
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_indexFragment_to_placeAutoCompleteFragment)
    }

    fun eliminar() {
        AlertDialog.Builder(
            context
        ).setTitle(R.string.titulo_eliminar)
            .setMessage(getString(R.string.mensaje_eliminar, ""))
            .setPositiveButton(R.string.boton_eliminar
            ) { _, _ ->
                /*indexViewModel.eliminarCiudad(this@IndexFragment.ciudad)
                    .observe(viewLifecycleOwner, Observer { returnedDeleted ->
                        if (returnedDeleted.result) {
                            idCiudad = returnedDeleted.primerId
                            activity?.colocarIdCiudad(idCiudad!!)
                        }
                    })*/
            }
            .setNegativeButton(R.string.boton_cancelar, null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        idCiudad = activity?.leerIdCiudad()
        indexViewModel =
            defaultViewModelProviderFactory.create(IndexViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.index_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_climas)
        val spinner = view.findViewById<Spinner>(R.id.spinner_ciudad)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val cardView = view.findViewById<View>(R.id.cardView_index)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        configurarSpinner(spinner)

        indexViewModel.getCiudades()
            .observe(this,
                Observer { ciudades ->
                    loadData = false
                    val adapter = spinner.adapter as ArrayAdapter<Ciudad>
                    adapter.addAll(ciudades)
                    adapter.notifyDataSetChanged()
                }
            )

        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(6)

        indexViewModel.getClima(idCiudad!!)
            .observe(viewLifecycleOwner,
            Observer { response ->
                if (!response.hasError){
                    val data = response.data
                    val dataBinding = DataBindingUtil.bind<ClimaCardBinding>(cardView)
                    dataBinding?.clima = data?.first()

                    val restData = data?.drop(1)
                    val climaAdapter = ClimaCardAdapter(restData!!)
                    recyclerView.adapter = climaAdapter

                    climaAdapter.notifyDataSetChanged()
                    progressBar.visibility = ProgressBar.GONE
                } else {
                    Toast.makeText(
                        context,
                        R.string.error_solicitud_clima,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        return view
    }

    fun configurarSpinner(spinner: Spinner){
        val adapter = ArrayAdapter(context!!,
            R.layout.support_simple_spinner_dropdown_item,
            mutableListOf<Ciudad>()
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (loadData) {
                    val ciudad = parent?.getItemAtPosition(position) as Ciudad
                    activity?.colocarIdCiudad(ciudad.id!!)

                    //Navegar
                    val navController = NavHostFragment.findNavController(this@IndexFragment)
                    navController.navigate(R.id.action_indexFragment_self)
                } else {
                  loadData = true
                }
            }
        }
    }

}