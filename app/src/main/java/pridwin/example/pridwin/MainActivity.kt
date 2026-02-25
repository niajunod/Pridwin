package pridwin.example.pridwin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import pridwin.data.weather.WeatherServiceLocator
import pridwin.navigation.AppNavGraph
import pridwin.ui.theme.PridwinTheme
import pridwin.work.WorkScheduler

class MainActivity : ComponentActivity() {

    private val requestPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // If user denies, app still works; notifications just won't post.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init service locator (so Worker + UI can safely use it)
        WeatherServiceLocator.init(applicationContext)

        // MUST: create notification channel (Android 8+)
        createNotificationChannel()

        // Schedule background task (WorkManager)
        WorkScheduler.schedule(applicationContext)

        // Ask notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge()

        setContent {
            var darkModeEnabled by rememberSaveable { mutableStateOf(false) }

            PridwinTheme(darkTheme = darkModeEnabled) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(
                        darkModeEnabled = darkModeEnabled,
                        onToggleDarkMode = { darkModeEnabled = it }
                    )
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PridwinNotifications.CHANNEL_ID,
                PridwinNotifications.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = PridwinNotifications.CHANNEL_DESC
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}

@Composable
private fun AppRoot(
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    AppNavGraph(
        navController = navController,
        darkModeEnabled = darkModeEnabled,
        onToggleDarkMode = onToggleDarkMode
    )
}

/**
 * IMPORTANT:
 * Your Worker/notification code MUST use this exact CHANNEL_ID.
 */
object PridwinNotifications {
    const val CHANNEL_ID = "pridwin_background_channel"
    const val CHANNEL_NAME = "Background Check"
    const val CHANNEL_DESC = "Notifications for background context checks"
}