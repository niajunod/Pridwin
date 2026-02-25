package pridwin.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pridwin.data.sensors.LightSensorMonitor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientControlsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lightMonitor = remember { LightSensorMonitor(context) }

    DisposableEffect(Unit) {
        lightMonitor.start()
        onDispose { lightMonitor.stop() }
    }

    val lux by lightMonitor.lux.collectAsState()
    val available = lightMonitor.isAvailable()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ambient Controls") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = if (available) "Light sensor: Available" else "Light sensor: Not available")
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Lux: ${lux ?: "—"}")
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "We just put this control for here, we weren't able to add actual hardware, but if you go into Extended Controls, it's very accurate and moves the same.")
        }
    }
}