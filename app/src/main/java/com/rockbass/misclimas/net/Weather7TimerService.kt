package com.rockbass.misclimas.net

import com.rockbass.misclimas.db.entities.ClimaResponse
import retrofit2.Call
import retrofit2.http.Query

interface Weather7TimerService {
    fun getClimas(
        @Query("lon") long: Double,
        @Query("lat") lat: Double,
        @Query("unit") unit: String = "metric",
        @Query("output") output:String = "json"
    ): Call<ClimaResponse>
}