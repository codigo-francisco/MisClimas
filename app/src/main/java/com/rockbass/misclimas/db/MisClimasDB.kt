package com.rockbass.misclimas.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rockbass.misclimas.DB_NAME
import com.rockbass.misclimas.db.dao.CiudadDAO
import com.rockbass.misclimas.db.entities.Ciudad

@Database(

    entities = [
        Ciudad::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MisClimasDB : RoomDatabase() {
    abstract fun ciudadDAO(): CiudadDAO
}