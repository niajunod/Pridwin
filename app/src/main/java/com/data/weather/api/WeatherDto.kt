// data/weather/api/WeatherDto.kt
package com.example.pridwin.data.weather.api

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("dt") val dt: Long? = null,
    @SerializedName("main") val main: MainDto? = null,
    @SerializedName("weather") val weather: List<WeatherConditionDto>? = null,
    @SerializedName("sys") val sys: SysDto? = null
)

data class MainDto(
    @SerializedName("temp") val temp: Double? = null
)

data class WeatherConditionDto(
    @SerializedName("main") val main: String? = null,
    @SerializedName("description") val description: String? = null
)

data class SysDto(
    @SerializedName("sunrise") val sunrise: Long? = null,
    @SerializedName("sunset") val sunset: Long? = null,
    @SerializedName("country") val country: String? = null
)