package com.razin.weather.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FunctionUtil {

    fun convertMillisToDateTime(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun convertMillisToTime(millis: Long): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun isTimeMoreThan(time1: Long, time2: Long): Boolean {
        val diff = time1 - time2
        return diff > 900000
    }
}