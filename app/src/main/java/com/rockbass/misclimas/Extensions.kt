package com.rockbass.misclimas

import android.app.Activity

fun Activity.colocarIdCiudad(idCiudad: Long) {
    this
        .getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Activity.MODE_PRIVATE)
        ?.edit()
        ?.putLong(CIUDAD_KEY, idCiudad)
        ?.apply()
}

fun Activity.leerIdCiudad(): Long {
    return this
        .getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Activity.MODE_PRIVATE)
        ?.getLong(CIUDAD_KEY, ID_CIUDAD_DEFAULT)?: ID_CIUDAD_DEFAULT
}