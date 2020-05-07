package com.rockbass.misclimas.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.rockbass.misclimas.R
import com.rockbass.misclimas.colocarIdCiudad
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> agregar()
            R.id.menu_delete -> eliminar()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = defaultViewModelProviderFactory
            .create(MainActivityViewModel::class.java)

        val spinner = findViewById<Spinner>(R.id.spinner_ciudad)
        val mapFragment = findViewById<View>(R.id.map_fragment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        mainActivityViewModel.getCiudades()
            .observe(this,
                Observer { ciudades ->
                    val adapter = ArrayAdapter(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        ciudades)
                    spinner.adapter = adapter
                }
            )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val ciudad = parent?.getItemAtPosition(position) as Ciudad
                colocarIdCiudad(ciudad.id!!)

                //Navegar
                val navController =  Navigation.findNavController(mapFragment)
                navController.navigate(R.id.action_indexFragment_self)
            }

        }

    }

    fun agregar(){

    }

    fun eliminar(){

    }
}
