package com.rockbass.misclimas.db.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

data class Temp2m (
    val max: Short,
    val min: Short
)

data class Data(
    val date: String,
    val weather: String,
    val temp2m: Temp2m
){
    private val localDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)

    fun getDiaDeLaSemana(): String{
        return localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .capitalize()
    }

    fun getFecha(): String {
        return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}

data class ClimaResponse(
    val dataseries: List<Data>
)

