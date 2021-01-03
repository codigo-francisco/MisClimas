package com.rockbass.misclimas.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rockbass.misclimas.db.dao.CiudadDAO
import javax.inject.Inject

class LoadViewModel @ViewModelInject constructor(
    private val ciudadDAO : CiudadDAO
): ViewModel(){

    fun cantidadCiudad() : LiveData<Long> = ciudadDAO.cantidadCiudad()
}