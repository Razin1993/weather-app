package com.razin.weather.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import coil.api.load
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.razin.weather.HomeActivity
import com.razin.weather.R
import com.razin.weather.api.ErrorType
import com.razin.weather.api.Resource
import com.razin.weather.data.dataStore.PreferencesUtil
import com.razin.weather.data.dataStore.dataStore
import com.razin.weather.data.db.WeatherInfo
import com.razin.weather.databinding.FragmentHomeBinding
import com.razin.weather.databinding.LayoutWeatherInfoBinding
import com.razin.weather.ui.uiUtils.DialogUtil
import com.razin.weather.ui.viewModel.WeatherViewModel
import com.razin.weather.util.FunctionUtil
import com.razin.weather.util.PermissionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil


class HomeFragment : Fragment(), AddCityFragment.CitySelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var weatherInfoBinding: LayoutWeatherInfoBinding? = null

    //location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: String? = null
    private var isLocationNotEnabled = false
    private var isSettingsClicked = false
    private var isDataLoadedFromRoom = false
    private var isDataAddedForCity = false

    //viewmodel
    private lateinit var viewModel: WeatherViewModel

    private lateinit var weatherInfo: WeatherInfo

    private var addedCity: List<String>? = null
    private lateinit var addCityDialog: AddCityFragment
    lateinit var listener: AddCityFragment.CitySelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherInfoBinding = binding.layoutWeatherInfo
        setListeners()
        viewModel = (activity as HomeActivity).viewModel
        handleObservers()
    }

    private fun requestPermission() {
        locationPermissionRequest.launch(PermissionUtil.REQUEST_LOCATION_PERMISSION)
    }

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            false
                        ) -> {
                    checkLocationEnabled()
                }

                else -> {
                    if (!::weatherInfo.isInitialized) {
                        binding.layoutPermissionDenied.handleVisibility(true)
                        if (isSettingsClicked) {
                            isSettingsClicked = false

                            lifecycleScope.launch {
                                if (PreferencesUtil.isLocationPermissionDeniedAlready(requireContext())) {
                                    DialogUtil.showPermissionRationaleDialog(
                                        getString(R.string.turn_on_location_permission),
                                        getString(R.string.location_permission_message_rationale),
                                        requireActivity()
                                    )
                                } else {
                                    if (!PermissionUtil.shouldShowRationaleForLocation(
                                            requireActivity()
                                        )
                                    ) {
                                        requireContext().dataStore.edit {
                                            it[PreferencesUtil.LOCATION_PERMISSION_DENIED_ALREADY] =
                                                true
                                        }
                                    }
                                }
                            }
                        } else if (addedCity.isNullOrEmpty() && !isDataAddedForCity) {
                            handleAddCity()
                        }
                    } else {
                        binding.cvLocationInfo.handleVisibility(true)
                    }

                }
            }

        }

    private fun handleAddCity() {
        addCityDialog = AddCityFragment(addedCity ?: emptyList(), listener)
        addCityDialog.show(parentFragmentManager, "add_city")
    }

    private fun checkLocationEnabled() {
        if (PermissionUtil.isLocationEnabled(requireContext())) {
            getCurrentLocation()
        } else {
            isLocationNotEnabled = true
            if (::weatherInfo.isInitialized) {
                binding.cvLocationInfo.handleVisibility(true)
            } else {
                binding.layoutPermissionDenied.handleVisibility(true)
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()

            result?.let {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(it.latitude, it.longitude, 1) { addresses ->
                        currentLocation = addresses[0].locality
                        handleWeatherForCurrentLocation(it.latitude, it.longitude)
                    }
                } else {
                    @Suppress("DEPRECATION") val addresses =
                        geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    addresses?.let { _ ->
                        currentLocation = addresses[0].locality
                        handleWeatherForCurrentLocation(it.latitude, it.longitude)
                    }
                }
            }
        }

    }

    private fun setListeners() {
        binding.root.setOnRefreshListener {
            isDataLoadedFromRoom = false
            handleFromRoom()
        }
        binding.settingsBtn.setOnClickListener {
            isSettingsClicked = true
            if (isLocationNotEnabled) {
                DialogUtil.showLocationSettingDialog(
                    getString(R.string.turn_on_location_service),
                    getString(R.string.location_service_message), requireActivity()
                )
            } else {
                requestPermission()
            }
        }

        binding.txtSettingsInfo.setOnClickListener {
            isSettingsClicked = true
            binding.cvLocationInfo.handleVisibility(false)
            if (isLocationNotEnabled) {
                DialogUtil.showLocationSettingDialog(
                    getString(R.string.turn_on_location_service),
                    getString(R.string.location_service_message), requireActivity()
                )
            } else {
                requestPermission()
            }

        }

        binding.txtCancelInfo.setOnClickListener {
            binding.cvLocationInfo.handleVisibility(false)
        }

        binding.addLocationIcon.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
            navController.navigate(R.id.cityListFragment, null, null)
        }

        binding.settingIcon.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
            navController.navigate(R.id.settingsFragment, null, null)
        }
    }

    private fun handleWeatherForCurrentLocation(lat: Double, long: Double) {
        binding.txtLocationName.text = currentLocation
        viewModel.getWeatherByLatLong(lat, long)
    }

    private fun handleObservers() {
        handleFromRoom()
        viewModel.weatherLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { weatherResponse ->
                        viewModel.saveWeatherForCurrentLocation(weatherResponse, true)
                        if (!::weatherInfo.isInitialized) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                handleFromRoom()
                                binding.txtInfoSwipe.handleVisibility(true)
                            }, 1000)
                        }
                    }
                }

                is Resource.Error -> {
                    if (response.message == ErrorType.NO_INTERNET.name) {
                        Toast.makeText(
                            requireContext(),
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("HomeFragment", "handleObservers: message ${response.message}")
                    }

                }

                is Resource.Loading -> {
                    Log.d("HomeFragment", "handleObservers: loading")
                }

            }
        }
    }

    private fun handleFromRoom() {
        viewModel.getLocations().observe(viewLifecycleOwner) { locations ->
            addedCity = locations
        }
        viewModel.getWeatherForCurrentLocation().observe(viewLifecycleOwner) { weatherList ->
            if (!isDataLoadedFromRoom) {
                if (!weatherList.isNullOrEmpty()) {
                    binding.layoutWeatherInfo.root.handleVisibility(true)
                    binding.layoutPermissionDenied.handleVisibility(false)
                    weatherInfo = weatherList[0]
                    handleWeatherInfo(weatherInfo)
                    isDataLoadedFromRoom = true
                    binding.root.isRefreshing = false
                    binding.txtInfoSwipe.handleVisibility(false)
                }
                if (PermissionUtil.isPermissionGrantedForLocation(requireContext())) {
                    checkLocationEnabled()
                } else {
                    requestPermission()
                }
            }

        }
    }

    private fun handleWeatherInfo(weatherInfo: WeatherInfo) {
        with(weatherInfo) {
            handleDateAndTime(date)
            val imageUrl = "https://openweathermap.org/img/wn/$icon.png"
            binding.layoutWeatherInfo.ivWeather.load(imageUrl)

            binding.layoutWeatherInfo.txtTemp.text = buildString {
                append(ceil(temperature).toInt())
                append("°")
            }

            binding.layoutWeatherInfo.txtInfo.text = buildString {
                append(main)
                append("(")
                append(description)
                append(")")
            }

            binding.layoutWeatherInfo.txtMin.text = buildString {
                append("Min : ")
                append(ceil(tempMin).toInt())
                append("°")
            }

            binding.layoutWeatherInfo.txtMax.text = buildString {
                append("Max : ")
                append(ceil(tempMax).toInt())
                append("°")
            }

            binding.layoutWeatherInfo.txtFeelsLikeValue.text = buildString {
                append(ceil(feelsLike).toInt())
                append("°")
            }

            binding.layoutWeatherInfo.txtHumidityValue.text = buildString {
                append(humidity)
                append("°")
            }

            handleWindDirection(windDeg)

            binding.layoutWeatherInfo.txtWindValue.text = buildString {
                append(windSpeed)
                append(" m/s")
            }

            binding.layoutWeatherInfo.txtAirPressureValue.text = buildString {
                append(pressure)
                append(" hPa")
            }
            binding.layoutWeatherInfo.txtSunRiseTime.text =
                FunctionUtil.convertMillisToTime(sunrise * 1000)
            binding.layoutWeatherInfo.txtSunSetTime.text =
                FunctionUtil.convertMillisToTime(sunset * 1000)
            handleSunSetRise()
        }
    }

    private fun handleWindDirection(windDeg: Int) {
        var direction = ""
        when {
            -22.5 <= windDeg && windDeg <= 22.5 -> {
                direction = "N Wind"
            }

            22.5 < windDeg && windDeg <= 67.5 -> {
                direction = "NE Wind"
            }

            67.5 < windDeg && windDeg <= 112.5 -> {
                direction = "E Wind"
            }

            112.5 < windDeg && windDeg <= 157.5 -> {
                direction = "SE Wind"
            }

            157.5 < windDeg && windDeg <= 202.5 -> {
                direction = "S Wind"
            }

            202.5 < windDeg && windDeg <= 247.5 -> {
                direction = "SW Wind"
            }

            247.5 < windDeg && windDeg <= 292.5 -> {
                direction = "W Wind"
            }

            292.5 < windDeg && windDeg <= 337.5 -> {
                direction = "NW Wind"
            }
        }

        binding.layoutWeatherInfo.txtWind.text = direction
    }

    private fun handleDateAndTime(date: Long) {
        binding.layoutWeatherInfo.txtToday.text = FunctionUtil.convertMillisToDateTime(date * 1000)
        if (FunctionUtil.isTimeMoreThan(System.currentTimeMillis(), date * 1000)) {
            binding.txtInfoSwipe.handleVisibility(true)
        }
    }

    private fun handleSunSetRise() {
        //just for sun set rise animation
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val currentHour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
                val percent = (currentHour.toFloat() / 2400) * 100
                val layoutSunSetRise =
                    requireActivity().findViewById<ConstraintLayout>(R.id.layoutSunSetRise)
                val initialConstraint = ConstraintSet()
                initialConstraint.clone(layoutSunSetRise)

                val finalConstraint = ConstraintSet()
                finalConstraint.clone(layoutSunSetRise)
                finalConstraint.connect(
                    R.id.ivSunAnimate,
                    ConstraintSet.START,
                    R.id.layoutSunSetRise,
                    ConstraintSet.START,
                    0
                )
                finalConstraint.connect(
                    R.id.ivSunAnimate,
                    ConstraintSet.END,
                    R.id.layoutSunSetRise,
                    ConstraintSet.END,
                    0
                )
                finalConstraint.setHorizontalBias(R.id.ivSunAnimate, percent)

                val transition = ChangeBounds()
                transition.interpolator = AccelerateDecelerateInterpolator()
                transition.duration = 3000

                TransitionManager.beginDelayedTransition(layoutSunSetRise, transition)
                finalConstraint.applyTo(layoutSunSetRise)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }, 2000)

    }

    private fun View.handleVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onCitySelected(selectedCity: String) {
        viewModel.getWeatherByCity(selectedCity)
        isDataAddedForCity = true
        addCityDialog.dismiss()
    }

}