package com.rockbass.misclimas.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity()
data class Ciudad(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=null,
    val name: String?,
    val longitude: Double?,
    val latitude: Double?,
    val altitude: Double?
)