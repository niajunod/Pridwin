package pridwin.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"


    const val CONTEXT = "context"


    const val PRIVACY = "privacy"

    const val FORECAST = "forecast/{role}"
    fun forecast(role: String) = "forecast/$role"
}