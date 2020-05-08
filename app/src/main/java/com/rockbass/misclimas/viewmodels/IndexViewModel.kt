package com.rockbass.misclimas.viewmodels

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.rockbass.misclimas.R
import com.rockbass.misclimas.adapters.ClimaCardAdapter
import com.rockbass.misclimas.databinding.ClimaCardBinding
import com.rockbass.misclimas.db.MisClimasDB
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.db.entities.ClimaResponse
import com.rockbass.misclimas.db.entities.Data
import com.rockbass.misclimas.net.climaService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IndexViewModel(application: Application) : AndroidViewModel(application){
    private val TAG = IndexViewModel::class.java.name
    private val db = MisClimasDB.getInstance(application)
    private val ciudadDao = db.ciudadDAO()

    data class ReturnedData(
        val data: List<Data>? = null,
        val hasError: Boolean = false,
        val errorMessage: String? = null,
        val ciudad: Ciudad? = null
    )

    enum class ReasonsNotDeleted {
        SOLO_UNA_CIUDAD
    }

    data class ReturnedDeleted (
        val result: Boolean,
        val reason: ReasonsNotDeleted? = null,
        val primerId: Long? = null
    )

    fun eliminarCiudad(idCiudad: Long): LiveData<ReturnedDeleted>{
        return liveData {
            val cantidad = ciudadDao.cantidadCiudades()
            if (cantidad > 1){
                ciudadDao.eliminarCiudad(idCiudad)
                val primerId = ciudadDao.primerId()
                emit(
                    ReturnedDeleted(
                        true,
                        primerId = primerId
                    )
                )
            }else{
                emit(
                    ReturnedDeleted(
                        false,
                        ReasonsNotDeleted.SOLO_UNA_CIUDAD
                    )
                )
            }
        }
    }

    fun getCiudades(): LiveData<List<Ciudad>> = ciudadDao.obtenerCiudades()

    fun getClima(idCiudad: Long) : LiveData<ReturnedData>{
        val liveData = MutableLiveData<ReturnedData>()

        viewModelScope.launch{
            val ciudad = ciudadDao.obtenerCiudad(idCiudad)
            climaService.getClimas(
                ciudad.longitude!!,
                ciudad.latitude!!
            ).enqueue(object: Callback<ClimaResponse> {
                override fun onFailure(call: Call<ClimaResponse>, t: Throwable) {
                    Log.e(TAG, t.message, t)
                    liveData.postValue(
                        ReturnedData(
                            hasError = true,
                            errorMessage = t.message
                        )
                    )
                }

                override fun onResponse(
                    call: Call<ClimaResponse>,
                    response: Response<ClimaResponse>
                ) {
                    if (response.isSuccessful){
                        val responseData = response.body()?.dataseries
                        liveData.postValue(
                            ReturnedData(
                                data = responseData,
                                ciudad = ciudad
                            )
                        )
                    }else{
                        liveData.postValue(
                            ReturnedData(
                                hasError = true,
                                errorMessage = response.message()
                            )
                        )
                    }
                }

            })
        }



        return liveData
    }

}