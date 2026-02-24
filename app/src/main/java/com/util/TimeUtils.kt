package com.example.pridwin.util


import kotlin.math.abs

object TimeUtils {
    fun isDay(dtSec: Long, sunriseSec: Long?, sunsetSec: Long?): Boolean {
        if (sunriseSec == null || sunsetSec == null) return true
        return dtSec in sunriseSec..sunsetSec
    }

    fun isFresh(nowMs: Long, lastUpdatedMs: Long, ttlMs: Long): Boolean {
        return abs(nowMs - lastUpdatedMs) <= ttlMs
    }
}