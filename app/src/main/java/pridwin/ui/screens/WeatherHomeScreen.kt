package pridwin.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import pridwin.data.profile.UserProfileStore
import pridwin.domain.model.Role
import pridwin.viewmodel.WeatherUiState
import pridwin.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    onOpenForecast: (String) -> Unit,
    onOpenAmbient: () -> Unit,
    modifier: Modifier = Modifier,
    vm: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val store = remember { UserProfileStore(context.applicationContext) }
    val scope = rememberCoroutineScope()

    val ui by vm.uiState.collectAsState()
    val hasPermission by vm.hasLocationPermission.collectAsState()

    val roles = remember { Role.values().toList() }

    // Load saved role (persistent)
    val savedRole by store.roleFlow.collectAsState(initial = roles.firstOrNull())
    var selectedRole by remember { mutableStateOf(savedRole) }

    LaunchedEffect(savedRole) {
        if (savedRole != null) selectedRole = savedRole
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        vm.onLocationPermissionResult(fine || coarse)
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

    val fg = if (isDay) Color(0xFF0F172A) else Color(0xFFF1F5F9)
    val fgMuted = fg.copy(alpha = 0.75f)
    val dividerColor = fg.copy(alpha = 0.30f)

    val chipUnselectedContainer =
        if (isDay) Color.White.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.14f)
    val chipSelectedContainer =
        if (isDay) Color(0xFF1E293B).copy(alpha = 0.12f) else Color.White.copy(alpha = 0.22f)
    val chipSelectedLabel = fg
    val chipUnselectedLabel = fgMuted

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Weather", color = fg) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = fg,
                    actionIconContentColor = fg,
                    navigationIconContentColor = fg
                )
            )
        }
    ) { inner ->
        Box(
            modifier = modifier
                .padding(inner)
                .fillMaxSize()
                .background(bgBrush)
        ) {
            when (val state = ui) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = fg
                    )
                }

                is WeatherUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = fg
                    )
                }

                is WeatherUiState.Ready -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.info.location,
                            style = MaterialTheme.typography.headlineSmall,
                            color = fg
                        )

                        Text(
                            text = "${"%.1f".format(state.info.temperatureC)} °C",
                            color = fgMuted
                        )

                        HorizontalDivider(color = dividerColor)

                        Text(
                            text = "Role",
                            style = MaterialTheme.typography.titleMedium,
                            color = fg
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            roles.take(3).forEach { role ->
                                val isSelected = (role == selectedRole)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedRole = role
                                        scope.launch { store.setRole(role) } // ✅ persist
                                    },
                                    label = {
                                        Text(
                                            text = prettyRole(role),
                                            color = if (isSelected) chipSelectedLabel else chipUnselectedLabel
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = chipUnselectedContainer,
                                        selectedContainerColor = chipSelectedContainer
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = dividerColor,
                                        selectedBorderColor = dividerColor
                                    )
                                )
                            }
                        }

                        if (roles.size > 3) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                roles.drop(3).forEach { role ->
                                    val isSelected = (role == selectedRole)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedRole = role
                                            scope.launch { store.setRole(role) } // ✅ persist
                                        },
                                        label = {
                                            Text(
                                                text = prettyRole(role),
                                                color = if (isSelected) chipSelectedLabel else chipUnselectedLabel
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = chipUnselectedContainer,
                                            selectedContainerColor = chipSelectedContainer
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = dividerColor,
                                            selectedBorderColor = dividerColor
                                        )
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = dividerColor)

                        Button(
                            onClick = {
                                val roleKey = selectedRole?.name ?: "UNKNOWN"
                                onOpenForecast(roleKey)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("5-day forecast")
                        }

                        Button(
                            onClick = onOpenAmbient,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ambient Controls")
                        }
                    }
                }
            }
        }
    }
}

private fun prettyRole(role: Role): String {
    val words = role.name
        .lowercase()
        .split('_')
        .filter { it.isNotBlank() }

    return words.joinToString(" ") { w ->
        w.replaceFirstChar { c -> c.uppercaseChar() }
    }
}