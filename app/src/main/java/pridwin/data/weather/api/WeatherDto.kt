package pridwin.data.weather.api

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("dt") val dt: Long? = null,
    @field:SerializedName("main") val main: MainDto? = null,
    @field:SerializedName("weather") val weather: List<WeatherConditionItemDto>? = null,
    @field:SerializedName("sys") val sys: SysDto? = null
)

data class MainDto(
    @field:SerializedName("temp") val temp: Double? = null
)

data class WeatherConditionItemDto(
    @field:SerializedName("main") val main: String? = null,
    @field:SerializedName("description") val description: String? = null
)

data class SysDto(
    @field:SerializedName("sunrise") val sunrise: Long? = null,
    @field:SerializedName("sunset") val sunset: Long? = null,
    @field:SerializedName("country") val country: String? = null
)