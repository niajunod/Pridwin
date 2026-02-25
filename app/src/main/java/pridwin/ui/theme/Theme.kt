// ui/theme/Theme.kt
package pridwin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PridwinSage,
    onPrimary = Color.White,

    secondary = PridwinRed,
    onSecondary = Color.White,

    tertiary = PridwinCream,
    onTertiary = PridwinDark,

    background = PridwinGrayBg,
    onBackground = PridwinDark,

    surface = PridwinCream,
    onSurface = PridwinDark,

    // optional but helps component defaults look consistent
    surfaceVariant = PridwinSage,
    onSurfaceVariant = PridwinDark,
    outline = PridwinDark.copy(alpha = 0.35f)
)

private val DarkColors = darkColorScheme(
    primary = PridwinSage,
    onPrimary = PridwinDarkBg,

    secondary = PridwinRed,
    onSecondary = Color.White,

    tertiary = PridwinCream,
    onTertiary = PridwinDark,

    background = PridwinDarkBg,
    onBackground = PridwinOnDark,

    surface = PridwinDarkSurface,
    onSurface = PridwinOnDark,

    surfaceVariant = PridwinDark,
    onSurfaceVariant = PridwinOnDark,
    outline = PridwinOnDark.copy(alpha = 0.30f)
)

@Composable
fun PridwinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}