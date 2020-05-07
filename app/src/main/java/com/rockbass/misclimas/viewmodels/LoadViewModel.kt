package com.rockbass.misclimas.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.rockbass.misclimas.db.MisClimasDB

class LoadViewModel(application: Application): AndroidViewModel(application){
    private val db = MisClimasDB.getInstance(application)
    private val ciudadDAO = db.ciudadDAO()

    fun cantidadCiudad() : LiveData<Long> = ciudadDAO.cantidadCiudad()
}