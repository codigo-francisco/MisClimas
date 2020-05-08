package com.rockbass.misclimas.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rockbass.misclimas.db.entities.Ciudad

@Dao
abstract class CiudadDAO {

    @Query("SELECT Count(id) FROM Ciudad")
    abstract suspend fun cantidadCiudades(): Long

    @Query("SELECT id FROM Ciudad LIMIT 1")
    abstract suspend fun primerId(): Long

    @Query("SELECT * FROM Ciudad WHERE id=:id")
    abstract suspend fun obtenerCiudad(id: Long): Ciudad

    @Query("SELECT COUNT(id) FROM Ciudad")
    abstract fun cantidadCiudad(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertarCiudad(ciudad: Ciudad): Long

    @Delete
    abstract suspend fun eliminarCiudad(ciudad: Ciudad)

    @Query("SELECT * FROM Ciudad")
    abstract fun obtenerCiudades(): LiveData<List<Ciudad>>
}