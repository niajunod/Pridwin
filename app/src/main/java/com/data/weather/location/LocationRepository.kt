package com.data.weather.location


import android.location.Location
import com.example.app.util.Result

interface LocationRepository {
    suspend fun getLocation(): Result<Location>
}