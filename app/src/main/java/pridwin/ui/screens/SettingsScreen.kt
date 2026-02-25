package pridwin.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import pridwin.work.WorkScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current

    var notificationsEnabled by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableFloatStateOf(0.5f) }

    val notificationsPermissionGranted = remember {
        mutableStateOf(isNotificationsPermissionGranted(context))
    }

    // Runtime permission launcher (Android 13+)
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        notificationsPermissionGranted.value = isNotificationsPermissionGranted(context)
    }

    LaunchedEffect(Unit) {
        notificationsPermissionGranted.value = isNotificationsPermissionGranted(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text("Preferences", style = MaterialTheme.typography.titleLarge)

            SettingRow(
                title = "Enable notifications",
                subtitle = "Allow background context alerts",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingRow(
                title = "Dark mode",
                subtitle = "Switch the app theme",
                checked = darkModeEnabled,
                onCheckedChange = { onToggleDarkMode(it) }
            )

            HorizontalDivider()

            Text("Notifications permission", style = MaterialTheme.typography.titleMedium)

            val permText = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                "Not required on this Android version."
            } else {
                if (notificationsPermissionGranted.value) "Granted" else "Not granted"
            }
            Text(permText, style = MaterialTheme.typography.bodyMedium)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !notificationsPermissionGranted.value
            ) {
                Button(
                    onClick = {
                        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Grant Notification Permission")
                }
            }

            HorizontalDivider()

            Text("Background Worker", style = MaterialTheme.typography.titleMedium)

            Text(
                "Runs periodically to refresh weather context even if the app is closed.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { WorkScheduler.runNow(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Run Background Check Now")
            }

            HorizontalDivider()

            Text("Volume (Demo Setting)", style = MaterialTheme.typography.titleMedium)

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )

            Text("Value: ${(sliderValue * 100).toInt()}%")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Done")
            }
        }
    }
}

private fun isNotificationsPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}