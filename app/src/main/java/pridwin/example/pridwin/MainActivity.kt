package pridwin.example.pridwin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import pridwin.navigation.AppNavGraph
import pridwin.ui.theme.PridwinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkModeEnabled by rememberSaveable { mutableStateOf(false) }

            PridwinTheme(darkTheme = darkModeEnabled) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(
                        darkModeEnabled = darkModeEnabled,
                        onToggleDarkMode = { darkModeEnabled = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRoot(
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    AppNavGraph(
        navController = navController,
        darkModeEnabled = darkModeEnabled,
        onToggleDarkMode = onToggleDarkMode
    )
}