// navigation/Routes.kt
package com.example.pridwin.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val DEBUG = "debug"
    const val DETAILS = "details/{id}"

    fun details(id: String): String = "details/$id"
}