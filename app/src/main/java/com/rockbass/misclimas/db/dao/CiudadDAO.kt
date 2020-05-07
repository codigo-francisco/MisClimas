package com.rockbass.misclimas.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rockbass.misclimas.db.entities.Ciudad

@Dao
abstract class CiudadDAO {

    @Query("SELECT * FROM Ciudad WHERE id=:id")
    abstract fun obtenerCiudad(id: Long): LiveData<Ciudad>

    @Query("SELECT COUNT(id) FROM Ciudad")
    abstract fun cantidadCiudad(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertarCiudad(ciudad: Ciudad): Long
}