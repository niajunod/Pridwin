package com.data.weather.location


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface LocationDataSource {
    suspend fun getLastKnownLocation(): Location?
}

/**
 * Uses Android's LocationManager only (no Google Play Services dependency).
 * Requires location permission granted by your PermissionGate.
 */
class AndroidLocationDataSource(
    private val context: Context
) : LocationDataSource {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Try quick path: last known from enabled providers
        val providers = lm.getProviders(true)
        val bestLastKnown = providers
            .mapNotNull { provider -> runCatching { lm.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { it.time }

        if (bestLastKnown != null) return bestLastKnown

        // Fallback: request a single update (may return null if device can't resolve)
        return suspendCancellableCoroutine { cont ->
            val provider = providers.firstOrNull()
            if (provider == null) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            val listener = android.location.LocationListener { loc ->
                if (cont.isActive) cont.resume(loc)
                lm.removeUpdates(this)
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