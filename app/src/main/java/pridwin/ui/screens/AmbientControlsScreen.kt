//Nia Junod & Alina Tarasevich
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pridwin.viewmodel.LightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientControlsScreen(
    onBack: () -> Unit,
    vm: LightViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

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
            Text(
                text = if (state.isAvailable)
                    "Light sensor: Available"
                else
                    "Light sensor: Not available"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Lux: ${state.lux ?: "—"}")

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Environment: ${state.bucket}")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ambient light is monitored in real time. This signal can be used to automatically adapt the user experience (e.g., dark mode in low light)."
            )
        }
    }
}