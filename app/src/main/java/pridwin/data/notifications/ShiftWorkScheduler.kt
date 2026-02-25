package pridwin.data.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ShiftWorkScheduler {

    private const val WORK_NAME = "shift_brief_work"

    fun scheduleDailyShiftBrief(context: Context) {
        val request = PeriodicWorkRequestBuilder<ShiftBriefWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(ShiftBriefWorker.defaultConstraints())
            .setInitialDelay(30, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelDailyShiftBrief(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}