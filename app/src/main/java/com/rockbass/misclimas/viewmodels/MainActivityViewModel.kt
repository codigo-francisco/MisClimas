package com.rockbass.misclimas.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.rockbass.misclimas.db.MisClimasDB
import com.rockbass.misclimas.db.entities.Ciudad

class MainActivityViewModel (application: Application): AndroidViewModel(application){
    val db = MisClimasDB.getInstance(application)
    val ciudadDAO = db.ciudadDAO()

    fun getCiudades(): LiveData<List<Ciudad>> = ciudadDAO.obtenerCiudades()
}