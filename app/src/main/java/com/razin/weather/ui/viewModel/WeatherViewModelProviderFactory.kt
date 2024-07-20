package com.razin.weather.ui.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.razin.weather.repository.WeatherRepository

class WeatherViewModelProviderFactory(
    val app: Application,
    val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(app, weatherRepository) as T
    }

}