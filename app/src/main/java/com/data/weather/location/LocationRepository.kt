package com.example.pridwin.data.weather.location

import android.location.Location
import com.example.pridwin.util.Result as AppResult

interface LocationRepository {
    suspend fun getLocation(): AppResult<Location>
}