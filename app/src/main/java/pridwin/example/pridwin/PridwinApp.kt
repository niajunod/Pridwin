//Nia Junod & Alina Tarasevich
package pridwin.example.pridwin

import android.app.Application
import pridwin.data.weather.WeatherServiceLocator

class PridwinApp : Application() {

    override fun onCreate() {
        super.onCreate()
        WeatherServiceLocator.init(applicationContext)
    }
}