package pridwin.data.weather.api

import com.google.gson.annotations.SerializedName

data class ForecastDto(
    @field:SerializedName("list")
    val list: List<ForecastItemDto>? = null,

    @field:SerializedName("city")
    val city: CityDto? = null
)

data class ForecastItemDto(
    @field:SerializedName("dt")
    val dt: Long? = null,

    @field:SerializedName("main")
    val main: ForecastMainDto? = null,

    @field:SerializedName("weather")
    val weather: List<ForecastWeatherConditionDto>? = null,

    @field:SerializedName("wind")
    val wind: WindDto? = null,

    @field:SerializedName("pop")
    val pop: Double? = null
)

data class ForecastMainDto(
    @field:SerializedName("temp")
    val temp: Double? = null
)

/**
 * Renamed to avoid redeclaration conflict with your other WeatherConditionDto in WeatherDto.kt.
 */
data class ForecastWeatherConditionDto(
    @field:SerializedName("main")
    val main: String? = null,

    @field:SerializedName("description")
    val description: String? = null
)

data class WindDto(
    @field:SerializedName("speed")
    val speed: Double? = null
)

data class CityDto(
    @field:SerializedName("name")
    val name: String? = null
)