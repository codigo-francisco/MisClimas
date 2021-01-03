package com.rockbass.misclimas.di

import android.content.Context
import androidx.room.Room
import com.rockbass.misclimas.DB_NAME
import com.rockbass.misclimas.db.MisClimasDB
import com.rockbass.misclimas.db.dao.CiudadDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMisClimasDB(@ApplicationContext appContext: Context) : MisClimasDB {
        return Room.databaseBuilder(
            appContext,
            MisClimasDB::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideCiudadDao(db: MisClimasDB): CiudadDAO {
        return db.ciudadDAO()
    }

}