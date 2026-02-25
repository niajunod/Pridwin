package com.example.pridwin.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val DEBUG = "debug"

    const val DETAILS = "details/{id}"
    fun details(id: String) = "details/$id"

    // NEW: forecast by role
    const val FORECAST = "forecast/{role}"
    fun forecast(role: String) = "forecast/$role"
}