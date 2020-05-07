package com.rockbass.misclimas.net

import com.rockbass.misclimas.WEATHER_API_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val retrofit = Retrofit.Builder()
    .baseUrl(WEATHER_API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val climaService = retrofit.create(Weather7TimerService::class.java)