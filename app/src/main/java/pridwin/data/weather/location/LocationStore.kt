//Nia Junod & Alina Tarasevich
package pridwin.data.weather.location

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "pridwin_prefs")

class LocationStore(private val context: Context) {

    companion object {
        private val KEY_LAT = doublePreferencesKey("last_lat")
        private val KEY_LON = doublePreferencesKey("last_lon")
    }

    suspend fun saveLastLocation(lat: Double, lon: Double) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAT] = lat
            prefs[KEY_LON] = lon
        }
    }

    suspend fun getLastLocation(): Pair<Double, Double>? {
        val prefs = context.dataStore.data.first()
        val lat = prefs[KEY_LAT]
        val lon = prefs[KEY_LON]
        return if (lat != null && lon != null) Pair(lat, lon) else null
    }
}