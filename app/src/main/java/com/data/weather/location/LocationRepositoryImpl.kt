package com.data.weather.location


import android.location.Location
import com.example.app.util.Result

class LocationRepositoryImpl(
    private val dataSource: LocationDataSource
) : LocationRepository {

    override suspend fun getLocation(): Result<Location> {
        return try {
            val loc = dataSource.getLastKnownLocation()
            if (loc == null) Result.Error("Location unavailable")
            else Result.Success(loc)
        } catch (t: Throwable) {
            Result.Error(message = t.message ?: "Location error", throwable = t)
        }
    }
}