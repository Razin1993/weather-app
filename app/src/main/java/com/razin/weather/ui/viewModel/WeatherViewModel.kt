package com.razin.weather.ui.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.razin.weather.MyApplication
import com.razin.weather.api.ErrorType
import com.razin.weather.api.Resource
import com.razin.weather.data.db.WeatherInfo
import com.razin.weather.model.WeatherResponse
import com.razin.weather.repository.WeatherRepository
import com.razin.weather.util.PermissionUtil
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class WeatherViewModel(
    app: Application, private val
    weatherRepository: WeatherRepository
) : AndroidViewModel(app) {

    val weatherLiveData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()

    fun getWeatherForCurrentLocation() = weatherRepository.getWeatherForCurrentLocation()

    fun getLocations() = weatherRepository.getLocationName()

    fun getAllWeather() = weatherRepository.getAllWeather()

    fun getWeatherByLatLong(lat: Double, long: Double) = viewModelScope.launch {
        handleWeatherByLatLong(lat, long)
    }

    fun getWeatherByCity(city: String) = viewModelScope.launch {
        handleWeatherByCity(city)
    }

    private suspend fun handleWeatherByLatLong(lat: Double, long: Double) {
        weatherLiveData.postValue(Resource.Loading())
        try {
            if (PermissionUtil.hasInternetConnection(getApplication<MyApplication>())) {
                val response = weatherRepository.getWeatherByLatLong(lat, long)
                weatherLiveData.postValue(handleWeatherResponse(response))
            } else {
                weatherLiveData.postValue(Resource.Error(ErrorType.NO_INTERNET.name))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherLiveData.postValue(Resource.Error(ErrorType.NETWORK_FAILURE.name))
                else -> weatherLiveData.postValue(Resource.Error(ErrorType.CONVERSION_ERROR.name))
            }
        }
    }

    private suspend fun handleWeatherByCity(city: String) {
        try {
            if (PermissionUtil.hasInternetConnection(getApplication<MyApplication>())) {
                val response = weatherRepository.getWeatherByCity(city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        saveWeatherForCurrentLocation(it, false)
                    }
                }
            } else {
                weatherLiveData.postValue(Resource.Error(ErrorType.NO_INTERNET.name))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherLiveData.postValue(Resource.Error(ErrorType.NETWORK_FAILURE.name))
                else -> weatherLiveData.postValue(Resource.Error(ErrorType.CONVERSION_ERROR.name))
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())

    }

    fun saveWeatherForCurrentLocation(weather: WeatherResponse, isCurrentLocation: Boolean) =
        viewModelScope.launch {
            weatherRepository.insertWeather(weather, isCurrentLocation)
        }

    fun deleteWeather(weather: WeatherInfo) = viewModelScope.launch {
        weatherRepository.deleteWeather(weather)
    }

    fun saveWeather(weather: WeatherInfo) = viewModelScope.launch {
        weatherRepository.insertWeather(weather)
    }
}