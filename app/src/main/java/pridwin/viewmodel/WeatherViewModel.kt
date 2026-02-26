//Nia Junod & Alina Tarasevich
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

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Ready(val info: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

sealed interface ForecastUiState {
    data object Idle : ForecastUiState
    data object Loading : ForecastUiState
    data class Ready(val days: List<ForecastDay>) : ForecastUiState
    data class Error(val message: String) : ForecastUiState
}

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _forecastUiState = MutableStateFlow<ForecastUiState>(ForecastUiState.Idle)
    val forecastUiState: StateFlow<ForecastUiState> = _forecastUiState.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(checkPermission())
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private var lastLat: Double? = null
    private var lastLon: Double? = null

    fun onLocationPermissionResult(granted: Boolean) {
        _hasLocationPermission.value = granted
        if (granted) refresh()
        else _uiState.value = WeatherUiState.Error("Location permission not granted.")
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
                lastLat = loc.latitude
                lastLon = loc.longitude

                val info = WeatherServiceLocator.repository.getCurrentWeather(
                    lat = loc.latitude,
                    lon = loc.longitude
                )

                _uiState.value = WeatherUiState.Ready(info)
            } catch (_: SecurityException) {
                _hasLocationPermission.value = false
                _uiState.value = WeatherUiState.Error("Location permission not granted.")
            } catch (t: Throwable) {
                _uiState.value = WeatherUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    fun refreshForecast() {
        viewModelScope.launch {
            if (!_hasLocationPermission.value) {
                _forecastUiState.value = ForecastUiState.Error("Location permission not granted.")
                return@launch
            }

            _forecastUiState.value = ForecastUiState.Loading

            try {
                val lat = lastLat
                val lon = lastLon

                val (useLat, useLon) = if (lat != null && lon != null) {
                    lat to lon
                } else {
                    val loc = getBestAvailableLocation()
                    lastLat = loc.latitude
                    lastLon = loc.longitude
                    loc.latitude to loc.longitude
                }

                val days = WeatherServiceLocator.repository.getFiveDayForecast(
                    lat = useLat,
                    lon = useLon
                )

                _forecastUiState.value = ForecastUiState.Ready(days)
            } catch (_: SecurityException) {
                _hasLocationPermission.value = false
                _forecastUiState.value = ForecastUiState.Error("Location permission not granted.")
            } catch (t: Throwable) {
                _forecastUiState.value = ForecastUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    private fun checkPermission(): Boolean {
        val ctx = getApplication<Application>()
        val fine = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED ||
                coarse == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getBestAvailableLocation(): Location {
        if (!checkPermission()) throw SecurityException("Missing location permission")

        val ctx = getApplication<Application>()
        val fused = LocationServices.getFusedLocationProviderClient(ctx)
        val token = CancellationTokenSource()

        return suspendCancellableCoroutine { cont ->
            try {
                fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) cont.resume(loc)
                        else cont.resumeWithException(IllegalStateException("Location unavailable."))
                    }
                    .addOnFailureListener { e ->
                        cont.resumeWithException(e)
                    }
            } catch (se: SecurityException) {
                cont.resumeWithException(se)
            }

            cont.invokeOnCancellation { token.cancel() }
        }
    }
}