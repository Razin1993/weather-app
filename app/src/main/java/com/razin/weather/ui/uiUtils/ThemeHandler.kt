package com.razin.weather.ui.uiUtils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHandler {

    fun handleTheme(context: Context){
        val sharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val selectedTheme = sharedPreferences.getString("theme", "system_default")

        when(selectedTheme){
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}