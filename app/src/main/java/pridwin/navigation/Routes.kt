package pridwin.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"

    // Repurposed from old Debug
    const val CONTEXT = "context"

    // New screen
    const val PRIVACY = "privacy"

    const val FORECAST = "forecast/{role}"
    fun forecast(role: String) = "forecast/$role"
}