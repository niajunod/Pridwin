package com.example.pridwin.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pridwin.viewmodel.WeatherUiState
import com.example.pridwin.viewmodel.WeatherViewModel

@Composable
fun WeatherHomeScreen(
    onOpenDetails: (String) -> Unit,
    onOpenForecast: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDebug: () -> Unit,
    modifier: Modifier = Modifier,
    vm: WeatherViewModel = viewModel()
) {
    val ui by vm.uiState.collectAsState()
    val hasPermission by vm.hasLocationPermission.collectAsState()

    val lightVm: LightViewModel = viewModel()
    val lightState by lightVm.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        vm.onLocationPermissionResult(granted = fine || coarse)
    }

    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            vm.refresh()
        }
    }

    val isDay = (ui as? WeatherUiState.Ready)?.info?.isDay == true
    val bgBrush = Brush.verticalGradient(
        colors = if (isDay)
            listOf(Color(0xFFE3F2FD), Color.White)
        else
            listOf(Color(0xFF0D1B2A), Color(0xFF1B263B))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("Settings") }
                    TextButton(onClick = onOpenDebug) { Text("Debug") }
                }
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(bgBrush)
        ) {
            when (val state = ui) {

                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is WeatherUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is WeatherUiState.Ready -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(state.info.location, style = MaterialTheme.typography.headlineSmall)
                        Text("${"%.1f".format(state.info.temperatureC)} °F")

                        Card {
                            Column(Modifier.padding(16.dp)) {
                                Text("Ambient Light")

                                if (!lightState.isAvailable) {
                                    Text("Sensor not available")
                                } else {
                                    Text("Lux: ${lightState.lux ?: "—"}")
                                    Text("Context: ${lightState.bucket}")
                                }
                            }
                        }

                        HorizontalDivider()

                        Button(
                            onClick = { onOpenDetails(state.info.location) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("More details")
                        }
                    }
                }
            }
        }
    }
}