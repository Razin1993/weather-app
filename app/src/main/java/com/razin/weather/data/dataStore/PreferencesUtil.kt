package com.razin.weather.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

object PreferencesUtil {
    val LOCATION_PERMISSION_DENIED_ALREADY = booleanPreferencesKey("location_permission_denied_already")

    suspend fun isLocationPermissionDeniedAlready(context: Context): Boolean {
        return context.dataStore.data.first()[LOCATION_PERMISSION_DENIED_ALREADY] ?: false
    }

}