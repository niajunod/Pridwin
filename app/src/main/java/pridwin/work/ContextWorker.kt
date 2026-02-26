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
import kotlinx.coroutines.flow.first
import pridwin.data.profile.UserProfileStore
import pridwin.domain.model.CommuteMode
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

            val store = UserProfileStore(applicationContext)

            val role = store.getRoleOnce()
            val commute = store.commuteModeFlow.first()
            val notificationsEnabled = store.getNotificationsEnabledOnce()

            if (!notificationsEnabled) {
                Log.d(tag, "Notifications disabled by user")
                return Result.success()
            }

            val message = buildContextMessage(role.name, commute.name)

            postNotificationSafe(
                title = "Shift Context Update",
                text = message
            )

            Result.success()
        } catch (t: Throwable) {
            Log.e(tag, "doWork() failed: ${t.message}", t)
            Result.failure()
        }
    }

    private fun buildContextMessage(role: String, commute: String): String {
        return when {
            commute.contains("BIKE") ->
                "Bike commute selected — check weather before leaving."

            role.contains("POOL") ->
                "Pool role active — monitor temperature and rain risk."

            else ->
                "Weather update for your shift at ${LocalDateTime.now().toLocalTime()}."
        }
    }

    private fun postNotificationSafe(title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        val notif = NotificationCompat.Builder(
            applicationContext,
            PridwinNotifications.CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(1001, notif)
    }
}