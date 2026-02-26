//Nia Junod & Alina Tarasevich
package pridwin.data.weather.location

import android.location.Location
import pridwin.util.Result as AppResult

interface LocationRepository {
    suspend fun getLocation(): AppResult<Location>
}