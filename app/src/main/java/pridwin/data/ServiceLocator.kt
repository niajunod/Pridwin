//Nia Junod & Alina Tarasevich
package pridwin.data

import pridwin.data.weather.WeatherRepository
import pridwin.data.weather.location.LocationRepository

object ServiceLocator {
    lateinit var locationRepository: LocationRepository
    lateinit var weatherRepository: WeatherRepository
}