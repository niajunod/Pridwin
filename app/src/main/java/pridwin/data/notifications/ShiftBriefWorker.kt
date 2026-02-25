package pridwin.data.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.WorkerParameters
import pridwin.data.weather.location.LocationStore
import pridwin.data.weather.WeatherServiceLocator

class ShiftBriefWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): ListenableWorker.Result {
        return try {
            val locationStore = LocationStore(applicationContext)
            val loc = locationStore.getLastLocation()
                ?: return ListenableWorker.Result.retry()

            val (lat, lon) = loc


            val repo = WeatherServiceLocator.repository

            val weather = repo.getCurrentWeather(lat = lat, lon = lon)

            val title = "Your Shift Weather Brief"
            val message = "${weather.temperatureC}°C • ${weather.condition}"

            NotificationHelper.createChannel(applicationContext)
            NotificationHelper.showShiftBrief(
                context = applicationContext,
                title = title,
                message = message
            )

            ListenableWorker.Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            ListenableWorker.Result.retry()
        }
    }

    companion object {
        fun defaultConstraints(): Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
    }
}