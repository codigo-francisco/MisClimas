package com.rockbass.misclimas.db.entities

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
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

    fun getDiaDeLaSemana(): String{
        val localDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)
        return localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            .capitalize()
    }

    fun getFecha(): String {
        val localDate = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)
        return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }
}

data class ClimaResponse(
    val dataseries: List<Data>
)

