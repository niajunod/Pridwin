// ui/theme/Theme.kt
package pridwin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
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

    // These help defaults (chips/cards/text fields) look consistent
    surfaceVariant = PridwinSage.copy(alpha = 0.18f),
    onSurfaceVariant = PridwinDark,
    outline = PridwinDark.copy(alpha = 0.35f)
)

private val DarkColors = darkColorScheme(
    // Primary buttons/chips use this. Keep onPrimary high-contrast.
    primary = PridwinSage,
    onPrimary = Color.Black,

    secondary = PridwinRed,
    onSecondary = Color.Black,

    tertiary = PridwinCream,
    onTertiary = PridwinDark,

    background = PridwinDarkBg,
    onBackground = PridwinOnDark,

    surface = PridwinDarkSurface,
    onSurface = PridwinOnDark,

    // IMPORTANT: surfaceVariant should be a DARK-ish surface, not a bright or mid-tone,
    // otherwise muted text/chips become low-contrast in dark mode.
    surfaceVariant = PridwinDarkSurface.copy(alpha = 0.85f),
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