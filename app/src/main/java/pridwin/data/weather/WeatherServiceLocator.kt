//Nia Junod & Alina Tarasevich
package pridwin.data.weather

import android.content.Context
import com.example.pridwin.R
import pridwin.data.weather.api.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object WeatherServiceLocator {

    lateinit var repository: WeatherRepository
        private set

    fun init(context: Context) {
        if (::repository.isInitialized) return

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApi = retrofit.create(WeatherApi::class.java)
        val apiKey = context.getString(R.string.openweather_api_key)

        repository = WeatherRepositoryImpl(
            api = weatherApi,
            apiKey = apiKey
        )
    }
}