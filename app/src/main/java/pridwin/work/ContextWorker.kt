package pridwin.work

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pridwin.example.pridwin.PridwinNotifications
import java.time.LocalDateTime

class ContextWorker(
    appContext: android.content.Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val tag = "ContextWorker"

        return try {
            Log.d(tag, "doWork() started")

            postNotificationSafe(
                title = "Background Check Complete",
                text = "Worker ran at ${LocalDateTime.now()}"
            )

            Log.d(tag, "doWork() success")
            Result.success()
        } catch (t: Throwable) {
            Log.e(tag, "doWork() failed: ${t.message}", t)

            postNotificationSafe(
                title = "Background Check Failed",
                text = t.message ?: "Unknown error"
            )

            Result.failure()
        }
    }

    private fun postNotificationSafe(title: String, text: String) {
        // Android 13+ requires POST_NOTIFICATIONS permission at runtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                Log.d("ContextWorker", "Notification not posted: POST_NOTIFICATIONS not granted")
                return
            }
        }

        val notif = NotificationCompat.Builder(applicationContext, PridwinNotifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(1001, notif)
        } catch (se: SecurityException) {
            Log.e("ContextWorker", "SecurityException posting notification", se)
        }
    }
}