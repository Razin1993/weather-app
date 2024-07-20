package com.razin.weather.repository

import com.razin.weather.api.RetrofitHelper
import com.razin.weather.data.db.WeatherDatabase
import com.razin.weather.data.db.WeatherInfo
import com.razin.weather.model.WeatherResponse

class WeatherRepository(val db: WeatherDatabase) {

    suspend fun getWeatherByLatLong(lat: Double, long: Double) =
        RetrofitHelper.api.getWeatherByLatLon(lat, long)

    suspend fun getWeatherByCity(city:String) =
        RetrofitHelper.api.getWeatherByCity(city)

    suspend fun insertWeather(weather: WeatherResponse, isCurrentLocation: Boolean) {
        val weatherInfo = WeatherInfo(
            weatherId = weather.weather[0].id,
            icon = weather.weather[0].icon,
            date = weather.dt.toLong(),
            temperature = weather.main.temp,
            tempMin = weather.main.temp_min,
            tempMax = weather.main.temp_max,
            humidity = weather.main.humidity,
            pressure = weather.main.pressure,
            windSpeed = weather.wind.speed,
            windDeg = weather.wind.deg,
            main = weather.weather[0].main,
            description = weather.weather[0].description,
            sunrise = weather.sys.sunrise,
            sunset = weather.sys.sunset,
            feelsLike = weather.main.feels_like,
            location = weather.name,
            isCurrentLocation = isCurrentLocation
        )

        insertWeather(weatherInfo)

    }

    fun getAllWeather() = db.weatherDao().getAllWeather()

    fun getWeatherForCurrentLocation() = db.weatherDao().getWeatherForCurrentLocation()

    fun getLocationName() = db.weatherDao().getLocations()

    suspend fun deleteWeather(weather: WeatherInfo) = db.weatherDao().deleteWeather(weather)

    suspend fun insertWeather(weather: WeatherInfo) = db.weatherDao().insertWeather(weather)
}