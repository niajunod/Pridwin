package com.example.pridwin.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pridwin.viewmodel.WeatherUiState
import pridwin.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    // Background gradient depends on day/night when we have Ready state
    val isDay = (ui as? WeatherUiState.Ready)?.info?.isDay == true
    val bgBrush = Brush.verticalGradient(
        colors = if (isDay) {
            listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF))
        } else {
            listOf(Color(0xFF0D1B2A), Color(0xFF1B263B))
        }
    )

    // Make text readable against background
    val contentColor = if (isDay) Color(0xFF0B1220) else Color.White

    Scaffold(
        modifier = modifier,
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
            CompositionLocalProvider(LocalContentColor provides contentColor) {

                if (!hasPermission) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Location permission is required to show local weather.")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }) {
                            Text("Grant permission")
                        }
                    }
                    return@CompositionLocalProvider
                }

                when (val state = ui) {
                    is WeatherUiState.Loading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Loading...")
                        }
                    }

                    is WeatherUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Couldn’t load weather", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text(state.message)

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "If you're on an emulator, set a mock location in Extended controls > Location, then retry.",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { vm.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }

                    is WeatherUiState.Ready -> {
                        val info = state.info

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(info.location, style = MaterialTheme.typography.headlineSmall)

                            // Fahrenheit display (since API now uses imperial)
                            Text(
                                text = "${"%.1f".format(info.temperatureC)} °F",
                                style = MaterialTheme.typography.displaySmall
                            )

                            Text(info.condition, style = MaterialTheme.typography.titleMedium)

                            Text(
                                text = if (info.isDay)
                                    "It’s daytime — expect conditions to warm up."
                                else
                                    "It’s night — temperatures may keep dropping.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Divider()

                            Text("Choose your role", style = MaterialTheme.typography.titleSmall)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenForecast("pool_server") },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Pool Server") }

                                OutlinedButton(
                                    onClick = { onOpenForecast("beach_server") },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Beach Server") }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenForecast("main_server") },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Main Server") }

                                OutlinedButton(
                                    onClick = { onOpenForecast("main_bartender") },
                                    modifier = Modifier.weight(1f)
                                ) { Text("Main Bartender") }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = { onOpenDetails(info.location) },
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
}