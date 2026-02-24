package com.example.pridwin.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pridwin.viewmodel.WeatherUiState
import com.example.pridwin.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    onOpenDetails: (String) -> Unit,
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

    LaunchedEffect(Unit) {
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
        ) {
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
                return@Box
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

                        Text(
                            text = "${"%.1f".format(info.temperatureC)} °C",
                            style = MaterialTheme.typography.displaySmall
                        )

                        Text(info.condition, style = MaterialTheme.typography.titleMedium)
                        Text(if (info.isDay) "Day" else "Night")

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