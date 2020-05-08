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

    companion object {
        @Synchronized
        fun getInstance(context: Context): MisClimasDB {
            if (instance==null){
                instance = Room.databaseBuilder(
                    context,
                    MisClimasDB::class.java,
                    DB_NAME
                ).build()
            }

            return instance as MisClimasDB
        }
    }
}

@Volatile
private var instance: MisClimasDB? = null