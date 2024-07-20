package com.razin.weather

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.razin.weather.data.db.WeatherDatabase
import com.razin.weather.databinding.ActivityHomeBinding
import com.razin.weather.repository.WeatherRepository
import com.razin.weather.ui.uiUtils.ThemeHandler
import com.razin.weather.ui.viewModel.WeatherViewModel
import com.razin.weather.ui.viewModel.WeatherViewModelProviderFactory

class HomeActivity : AppCompatActivity() {

    private var _homeBinding: ActivityHomeBinding? = null
    private val homeBinding get() = _homeBinding!!

    lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHandler.handleTheme(this)
        super.onCreate(savedInstanceState)
        _homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(homeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(homeBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val weatherRepository = WeatherRepository(WeatherDatabase(this))
        val viewModelProvider = WeatherViewModelProviderFactory(application, weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProvider)[WeatherViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _homeBinding = null
    }
}