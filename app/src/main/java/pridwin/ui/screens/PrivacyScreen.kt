//Nia Junod & Alina Tarasevich
package pridwin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy by Design") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "How we handle permissions and data",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "This app is designed to use the minimum data needed to provide context-aware shift guidance. "
                        + "We avoid background tracking and do not collect personal identifiers.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Location") {
                    Bullet("Permissions: ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION.")
                    Bullet("Purpose: get an approximate device location to fetch local weather and sunrise/sunset times.")
                    Bullet("Scope: location is accessed only when you refresh weather/forecast (foreground use).")
                    Bullet("Minimization: we use lat/lon only to request weather; we do not compute or store routes, places visited, or continuous history.")
                    Bullet("Storage: the app does not save raw coordinates to disk.")
                    Bullet("User control: you can deny location permission; the app can still work if you use a default/location-less experience (if enabled in your build).")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Network & third-party services") {
                    Bullet("Permission: INTERNET.")
                    Bullet("Purpose: call the weather service to retrieve current conditions and a 5-day forecast.")
                    Bullet("Data sent: latitude/longitude (or city fallback), units, and API key to the weather provider.")
                    Bullet("Data received: forecast values such as temperature, precipitation probability, wind, and condition text.")
                    Bullet("Transport: requests are made over HTTPS through Retrofit.")
                    Bullet("No advertising/analytics SDKs are included by design.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Notifications") {
                    Bullet("Permission: POST_NOTIFICATIONS (Android 13+).")
                    Bullet("Purpose: optional alerts (for example, a weather-based reminder).")
                    Bullet("User control: notifications are optional and can be disabled in Settings or system settings.")
                    Bullet("No sensitive content: notifications should avoid showing precise location.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Local data storage") {
                    Bullet("We store only app preferences locally using DataStore (role selection, theme, and toggles).")
                    Bullet("We do not collect names, emails, phone numbers, or contact lists.")
                    Bullet("We do not upload your preferences to a server.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Section("Logging & safety") {
                    Bullet("We avoid logging sensitive values such as raw latitude/longitude.")
                    Bullet("We handle missing permissions gracefully (show error states instead of crashing).")
                    Bullet("You can clear the app’s local data via Android settings to reset preferences.")
                }
            }

            Spacer(Modifier.height(6.dp))
            HorizontalDivider()
            Spacer(Modifier.height(2.dp))

            Text(
                "Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "We prioritize minimal data collection, foreground-only context access, and local-only storage. "
                        + "Your data is used to compute contextual shift guidance, not to build a profile.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun Bullet(text: String) {
    Text("• $text", style = MaterialTheme.typography.bodyMedium)
}