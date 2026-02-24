// di/AppModule.kt
package com.example.pridwin;

import android.content.Context
import com.example.pridwin.data.weather.settings.SettingsDataStore
import com.example.pridwin.data.weather.settings.SettingsRepository
import com.example.pridwin.data.weather.settings.SettingsRepositoryImpl
import okhttp3.OkHttpClient
import pridwin.data.weather.WeatherRepositoryImpl
import pridwin.data.weather.api.WeatherApi
import pridwin.data.weather.cache.WeatherCache
import pridwin.data.weather.location.AndroidLocationDataSource
import pridwin.data.weather.location.LocationDataSource
import pridwin.data.weather.location.LocationRepository
import pridwin.data.weather.location.LocationRepositoryImpl
import pridwin.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.WEATHER_BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApi: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }

    private val weatherCache: WeatherCache by lazy {
        WeatherCache()
    }

    val weatherRepository: WeatherRepositoryImpl by lazy {
        WeatherRepositoryImpl(
            api = weatherApi,
            cache = weatherCache
        )
    }

    fun locationRepository(context: Context): LocationRepository {
        val ds: LocationDataSource = AndroidLocationDataSource(context.applicationContext)
        return LocationRepositoryImpl(ds)
    }

    fun settingsRepository(context: Context): SettingsRepository {
        val ds = SettingsDataStore(context.applicationContext)
        return SettingsRepositoryImpl(ds)
    }
}