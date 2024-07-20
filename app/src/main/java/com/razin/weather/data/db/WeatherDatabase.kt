package com.razin.weather.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherInfo::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDAO

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        private val OBJ = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(OBJ) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            "weather_db"
        ).build()

    }

}