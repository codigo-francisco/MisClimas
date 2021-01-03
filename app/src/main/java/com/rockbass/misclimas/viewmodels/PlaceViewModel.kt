package com.rockbass.misclimas.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.rockbass.misclimas.db.MisClimasDB
import com.rockbass.misclimas.db.dao.CiudadDAO
import com.rockbass.misclimas.db.entities.Ciudad
import javax.inject.Inject

class PlaceViewModel @ViewModelInject constructor(
    private val ciudadDAO : CiudadDAO
) : ViewModel() {

    fun insertarCiudad(ciudad: Ciudad): LiveData<Long> {
        return liveData {
            val id = ciudadDAO.insertarCiudad(ciudad)
            emit(id)
        }
    }
}