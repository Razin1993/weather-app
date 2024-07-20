package com.razin.weather.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherInfo)

    @Query("SELECT * FROM weather")
    fun getAllWeather(): LiveData<List<WeatherInfo>>

    @Query("SELECT * FROM weather where isCurrentLocation = 1 LIMIT 1")
    fun getWeatherForCurrentLocation(): LiveData<List<WeatherInfo>>

    @Query("SELECT location FROM weather")
    fun getLocations(): LiveData<List<String>>

    @Delete
    suspend fun deleteWeather(weather: WeatherInfo)

}