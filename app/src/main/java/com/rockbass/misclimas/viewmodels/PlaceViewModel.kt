package com.rockbass.misclimas.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.rockbass.misclimas.db.MisClimasDB
import com.rockbass.misclimas.db.entities.Ciudad

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MisClimasDB.getInstance(application)
    private val ciudadDAO = db.ciudadDAO()
    private val insertarLiveData: LiveData<Long> = MutableLiveData()

    fun insertarCiudad(ciudad: Ciudad): LiveData<Long> {
        return liveData {
            val id = ciudadDAO.insertarCiudad(ciudad)
            emit(id)
        }
    }
}