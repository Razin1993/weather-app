package com.razin.weather.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionUtil {

    val REQUEST_LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    fun isPermissionGrantedForLocation(context: Context) : Boolean {
       return ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRationaleForLocation(activity: Activity) : Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun isLocationEnabled(context: Context) : Boolean {
        val locationManager  = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isGpsEnabled || isNetWorkEnabled
    }

    fun launchPermissionSettings(activity: Activity){
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
            it.data = Uri.fromParts("package",activity.packageName,null)
        }
        activity.startActivity(settingsIntent)
    }

    fun launchLocationSetting(activity: Activity){
        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(settingsIntent)
    }

    fun hasInternetConnection(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?:return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}