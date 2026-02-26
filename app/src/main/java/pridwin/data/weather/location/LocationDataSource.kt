//Nia Junod & Alina Tarasevich
package pridwin.data.weather.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface LocationDataSource {
    suspend fun getLastKnownLocation(): Location?
}

class AndroidLocationDataSource(
    private val context: Context
) : LocationDataSource {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val providers = lm.getProviders(true)
        val bestLastKnown = providers
            .mapNotNull { provider -> runCatching { lm.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { it.time }

        if (bestLastKnown != null) return bestLastKnown

        return suspendCancellableCoroutine { cont ->
            val provider = providers.firstOrNull()
            if (provider == null) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    if (cont.isActive) cont.resume(loc)
                    lm.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            try {
                lm.requestSingleUpdate(provider, listener, context.mainLooper)
                cont.invokeOnCancellation { lm.removeUpdates(listener) }
            } catch (_: Throwable) {
                cont.resume(null)
            }
        }
    }
}