package pridwin.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import pridwin.data.weather.WeatherServiceLocator
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.WeatherInfo
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ---------- Current Weather UI State ----------
sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Ready(val info: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

// ---------- Forecast UI State (Option A) ----------
sealed interface ForecastUiState {
    data object Idle : ForecastUiState
    data object Loading : ForecastUiState
    data class Ready(val days: List<ForecastDay>) : ForecastUiState
    data class Error(val message: String) : ForecastUiState
}

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    // Current weather
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    // Permissions
    private val _hasLocationPermission = MutableStateFlow(checkPermission())
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    // Option A: store last coordinates so ForecastScreen can reuse them
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    // Forecast state
    private val _forecastUiState = MutableStateFlow<ForecastUiState>(ForecastUiState.Idle)
    val forecastUiState: StateFlow<ForecastUiState> = _forecastUiState.asStateFlow()

    fun onLocationPermissionResult(granted: Boolean) {
        _hasLocationPermission.value = granted
        if (granted) refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            if (!_hasLocationPermission.value) {
                _uiState.value = WeatherUiState.Error("Location permission not granted.")
                return@launch
            }

            _uiState.value = WeatherUiState.Loading

            try {
                val loc = getBestAvailableLocation()

                // Save coords for Option A forecast screen
                lastLat = loc.latitude
                lastLon = loc.longitude

                val info = WeatherServiceLocator.repository.getCurrentWeather(
                    lat = loc.latitude,
                    lon = loc.longitude
                )

                _uiState.value = WeatherUiState.Ready(info)

            } catch (se: SecurityException) {
                _uiState.value =
                    WeatherUiState.Error("Location permission missing or revoked. Please grant permission and retry.")
            } catch (t: Throwable) {
                _uiState.value = WeatherUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    /**
     * Called by ForecastScreen (Option A).
     * Uses existing repository method: getFiveDayForecast(lat, lon): List<ForecastDay>
     */
    fun refreshForecast() {
        viewModelScope.launch {
            if (!_hasLocationPermission.value) {
                _forecastUiState.value = ForecastUiState.Error("Location permission not granted.")
                return@launch
            }

            val lat = lastLat
            val lon = lastLon
            if (lat == null || lon == null) {
                _forecastUiState.value = ForecastUiState.Error("No location yet. Go back and retry.")
                return@launch
            }

            _forecastUiState.value = ForecastUiState.Loading

            try {
                val days = WeatherServiceLocator.repository.getFiveDayForecast(
                    lat = lat,
                    lon = lon
                )
                _forecastUiState.value = ForecastUiState.Ready(days)
            } catch (t: Throwable) {
                _forecastUiState.value = ForecastUiState.Error(t.message ?: "Forecast failed")
            }
        }
    }

    private fun checkPermission(): Boolean {
        val ctx = getApplication<Application>()
        val fine = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getBestAvailableLocation(): Location {
        val ctx = getApplication<Application>()
        val fused = LocationServices.getFusedLocationProviderClient(ctx)

        val token = CancellationTokenSource()
        val current: Location? = suspendCancellableCoroutine { cont ->
            try {
                fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { e -> cont.resumeWithException(e) }
            } catch (se: SecurityException) {
                cont.resumeWithException(se)
            }

            cont.invokeOnCancellation { token.cancel() }
        }

        if (current != null) return current

        val last: Location? = suspendCancellableCoroutine { cont ->
            try {
                fused.lastLocation
                    .addOnSuccessListener { loc -> cont.resume(loc) }
                    .addOnFailureListener { e -> cont.resumeWithException(e) }
            } catch (se: SecurityException) {
                cont.resumeWithException(se)
            }
        }

        return last ?: throw IllegalStateException(
            "Location unavailable. If you're on the emulator: Extended controls > Location > Send, then retry."
        )
    }
}