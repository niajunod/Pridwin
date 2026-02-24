package com.util


import kotlin.math.abs

object TimeUtils {

    /**
     * OpenWeather provides:
     * - dt: current time (seconds)
     * - sunrise/sunset: seconds
     */
    fun isDay(dtSec: Long, sunriseSec: Long?, sunsetSec: Long?): Boolean {
        if (sunriseSec == null || sunsetSec == null) return true
        return dtSec in sunriseSec..sunsetSec
    }

    /**
     * Simple "fresh enough" check for caches.
     */
    fun isFresh(nowMs: Long, lastUpdatedMs: Long, ttlMs: Long): Boolean {
        return abs(nowMs - lastUpdatedMs) <= ttlMs
    }
}