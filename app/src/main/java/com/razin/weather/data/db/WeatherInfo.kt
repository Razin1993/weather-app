package com.razin.weather.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "weather")
data class WeatherInfo(
    @PrimaryKey
    val location: String,
    val weatherId: Int,
    val icon: String,
    val date: Long,
    val temperature: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val main: String,
    val description: String,
    val windDeg: Int,
    val sunrise: Long,
    val sunset: Long,
    val feelsLike: Double,
    val isCurrentLocation: Boolean = false
) : Serializable