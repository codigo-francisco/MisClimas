package com.rockbass.misclimas.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.rockbass.misclimas.*
import com.rockbass.misclimas.adapters.ClimaCardAdapter
import com.rockbass.misclimas.databinding.ClimaCardBinding
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.helpers.colocarIdCiudad
import com.rockbass.misclimas.helpers.leerIdCiudad
import com.rockbass.misclimas.helpers.setSelectionById
import com.rockbass.misclimas.viewmodels.IndexViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IndexFragment: Fragment() {

    private var idCiudad: Long? = null
    private var ciudad: Ciudad? = null
    private val indexViewModel: IndexViewModel by viewModels()
    private var isNavigation = false
    private lateinit var progressBar: ProgressBar
    private lateinit var cardView: View
    private lateinit var recyclerView: RecyclerView

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

    private fun agregar() {
        //Navegamos para agregar ciudades
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_indexFragment_to_placeAutoCompleteFragment)
    }

    private fun eliminar() {
        AlertDialog.Builder(
            context
        ).setTitle(R.string.titulo_eliminar)
            .setMessage(getString(R.string.mensaje_eliminar, ciudad?.name))
            .setPositiveButton(R.string.boton_eliminar
            ) { _, _ ->
                indexViewModel.eliminarCiudad(this@IndexFragment.idCiudad!!)
                    .observe(viewLifecycleOwner, { returnedDeleted ->
                        if (returnedDeleted.result) {
                            idCiudad = returnedDeleted.primerId
                            activity?.colocarIdCiudad(idCiudad!!)

                            navegarAIndex()
                        }else{
                            if (returnedDeleted.reason == IndexViewModel.ReasonsNotDeleted.SOLO_UNA_CIUDAD){
                                Snackbar
                                    .make(requireView(), R.string.mensaje_no_eliminado_una_ciudad, Snackbar.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                )
            }
            .setNegativeButton(R.string.boton_cancelar, null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        idCiudad = activity?.leerIdCiudad()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.index_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_climas)
        val spinner = view.findViewById<Spinner>(R.id.spinner_ciudad)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        cardView = view.findViewById(R.id.cardView_index)
        progressBar = view.findViewById(R.id.progressBar)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        configurarSpinner(spinner)

        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(6)

        return view
    }

    private fun configurarSpinner(spinner: Spinner){
        indexViewModel.getCiudades()
            .observe(viewLifecycleOwner,
                { ciudades ->
                    val adapter = ArrayAdapter(requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        ciudades
                    )

                    spinner.adapter = adapter

                    spinner.setSelectionById(activity?.leerIdCiudad()!!)
                }
            )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isNavigation) {
                    val ciudad = parent?.getItemAtPosition(position) as Ciudad
                    activity?.colocarIdCiudad(ciudad.id!!)

                    navegarAIndex()
                } else {
                    getClima()
                    isNavigation = true
                }
            }
        }
    }

    fun getClima(){
        indexViewModel.getClima(idCiudad!!)
            .observe(viewLifecycleOwner,
                { response ->
                    if (!response.hasError){
                        ciudad = response.ciudad
                        val data = response.data
                        val climaCard = ClimaCardBinding.bind(cardView)
                        climaCard.clima = data?.first()

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
    }

    fun navegarAIndex(){
        val navController = NavHostFragment.findNavController(this@IndexFragment)
        navController.navigate(R.id.action_indexFragment_self)
    }

}