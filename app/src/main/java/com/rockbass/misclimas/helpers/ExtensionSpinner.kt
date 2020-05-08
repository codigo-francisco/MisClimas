package com.rockbass.misclimas.helpers

import android.widget.Spinner
import com.rockbass.misclimas.db.entities.Ciudad

fun Spinner.setSelectionById(id: Long){
    loop@ for (i in 0 until count){
        val ciudad = getItemAtPosition(i) as Ciudad
        if (ciudad.id == id){
            setSelection(i)
            break@loop
        }
    }
}