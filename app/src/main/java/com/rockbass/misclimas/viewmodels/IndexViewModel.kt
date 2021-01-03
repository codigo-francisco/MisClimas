package com.rockbass.misclimas.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.rockbass.misclimas.db.dao.CiudadDAO
import com.rockbass.misclimas.db.entities.Ciudad
import com.rockbass.misclimas.db.entities.ClimaResponse
import com.rockbass.misclimas.db.entities.Data
import com.rockbass.misclimas.net.Weather7TimerService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class IndexViewModel @ViewModelInject constructor(
    private val ciudadDao : CiudadDAO,
    private val climaService: Weather7TimerService
) : ViewModel(){

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

                    Timber.e(t)

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