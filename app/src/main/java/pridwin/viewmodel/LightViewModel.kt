//Nia Junod & Alina Tarasevich
package pridwin.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LightUiState(
    val isAvailable: Boolean = false,
    val lux: Float? = null,
    val bucket: String = "—"
)

class LightViewModel(app: Application) : AndroidViewModel(app), SensorEventListener {

    private val sensorManager =
        app.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val lightSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _uiState = MutableStateFlow(
        LightUiState(isAvailable = lightSensor != null)
    )
    val uiState: StateFlow<LightUiState> = _uiState.asStateFlow()

    init {
        lightSensor?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent?) {
        val lux = event?.values?.firstOrNull() ?: return
        _uiState.value = LightUiState(
            isAvailable = true,
            lux = lux,
            bucket = bucketForLux(lux)
        )
    }

    private fun bucketForLux(lux: Float): String = when {
        lux < 10f -> "Very dark"
        lux < 50f -> "Dim"
        lux < 200f -> "Indoor"
        lux < 1000f -> "Bright"
        else -> "Direct sun"
    }
}