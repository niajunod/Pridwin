package com.example.pridwin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pridwin.ui.screens.WeatherHomeScreen
import pridwin.data.profile.SettingsScreen
import pridwin.ui.screens.DebugScreen
import pridwin.ui.screens.DetailsScreen

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
    }
}