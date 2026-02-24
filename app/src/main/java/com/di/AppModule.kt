package com.example.pridwin.di

import android.content.Context
import com.example.pridwin.data.cache.WeatherCache
import com.example.pridwin.data.location.AndroidLocationDataSource
import com.example.pridwin.data.location.LocationDataSource
import com.example.pridwin.data.location.LocationRepository
import com.example.pridwin.data.location.LocationRepositoryImpl
import com.example.pridwin.data.settings.SettingsDataStore
import com.example.pridwin.data.settings.SettingsRepository
import com.example.pridwin.data.settings.SettingsRepositoryImpl
import com.example.pridwin.data.weather.WeatherRepository
import com.example.pridwin.data.weather.WeatherRepositoryImpl
import com.data.weather.api.WeatherApi
import com.example.pridwin.util.Constants
import okhttp3.OkHttpClient
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

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(weatherApi, weatherCache)
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