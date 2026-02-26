//Nia Junod & Alina Tarasevich
package pridwin.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pridwin.ui.screens.AmbientControlsScreen
import pridwin.ui.screens.ContextLogicScreen
import pridwin.ui.screens.ForecastScreen
import pridwin.ui.screens.PrivacyScreen
import pridwin.ui.screens.SettingsScreen
import pridwin.ui.screens.WeatherHomeScreen
import pridwin.viewmodel.WeatherViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME) {
                WeatherHomeScreen(
                    onOpenForecast = { role ->
                        navController.navigate(Routes.forecast(role))
                    },
                    onOpenAmbient = {
                        navController.navigate(AmbientNav.AMBIENT)
                    }
                )
            }

            composable(Routes.CONTEXT) {
                ContextLogicScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.PRIVACY) {
                PrivacyScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    darkModeEnabled = darkModeEnabled,
                    onToggleDarkMode = onToggleDarkMode
                )
            }

            composable(
                route = Routes.FORECAST,
                arguments = listOf(navArgument("role") { type = NavType.StringType })
            ) { backStackEntry ->
                val role = backStackEntry.arguments?.getString("role") ?: "default"

                val homeEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Routes.HOME)
                }
                val weatherVm: WeatherViewModel = viewModel(homeEntry)

                ForecastScreen(
                    role = role,
                    onBack = { navController.popBackStack() },
                    weatherVm = weatherVm
                )
            }

            composable(AmbientNav.AMBIENT) {
                AmbientControlsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}