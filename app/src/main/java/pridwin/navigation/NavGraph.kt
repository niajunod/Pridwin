package com.example.pridwin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pridwin.ui.screens.SettingsScreen
import com.example.pridwin.ui.screens.WeatherHomeScreen
import pridwin.ui.screens.DebugScreen
import pridwin.ui.screens.DetailsScreen
import pridwin.ui.screens.ForecastScreen
import pridwin.viewmodel.WeatherViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            WeatherHomeScreen(
                onOpenDetails = { id -> navController.navigate(Routes.details(id)) },
                onOpenForecast = { role -> navController.navigate(Routes.forecast(role)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenDebug = { navController.navigate(Routes.DEBUG) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.DEBUG) {
            DebugScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailsScreen(id = id, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.FORECAST,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "default"

            // Share the WeatherViewModel created for the HOME destination
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
    }
}