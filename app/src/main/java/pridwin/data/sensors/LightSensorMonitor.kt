package pridwin.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LightSensorMonitor(context: Context) : SensorEventListener {

    private val appContext = context.applicationContext

    private val sensorManager =
        appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val lightSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _lux = MutableStateFlow<Float?>(null)
    val lux: StateFlow<Float?> = _lux.asStateFlow()

    fun isAvailable(): Boolean = lightSensor != null

    fun start() {
        val sensor = lightSensor
        Log.d("LIGHT", "start() called. sensor=${sensor?.name}, available=${sensor != null}")
        if (sensor == null) {
            _lux.value = null
            return
        }

        val ok = sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )

        Log.d("LIGHT", "registerListener returned: $ok")
    }

    fun stop() {
        Log.d("LIGHT", "stop() called")
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_LIGHT) return
        val v = event.values.firstOrNull()
        _lux.value = v
        Log.d("LIGHT", "Lux: $v")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}