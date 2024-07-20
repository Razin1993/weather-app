package com.razin.weather.api

import com.razin.weather.BuildConfig
import com.razin.weather.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeatherByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en",
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en",
        @Query("appid") apiKey: String = BuildConfig.API_KEY
    ): Response<WeatherResponse>
}