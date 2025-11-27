package id.antasari.p7_modern_ui_230104040213.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ================================
//        DARK COLOR SCHEME
// ================================
private val DarkColorScheme = darkColorScheme(
    primary = Gold80,
    onPrimary = Black80,
    primaryContainer = Gold60,
    onPrimaryContainer = Black80,

    secondary = White80,
    onSecondary = Black80,
    secondaryContainer = Black60,
    onSecondaryContainer = White80,

    tertiary = Black80,
    onTertiary = White80,
    tertiaryContainer = Black60,
    onTertiaryContainer = White80,

    background = Black80,
    onBackground = White80,

    surface = Black60,
    onSurface = White80,

    error = ErrorRedDark,
    onError = White80
)


// ================================
//        LIGHT COLOR SCHEME
// ================================
private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = BlackPrimary,
    primaryContainer = GoldSoft,
    onPrimaryContainer = BlackPrimary,

    secondary = WhitePrimary,
    onSecondary = BlackPrimary,
    secondaryContainer = WhiteMuted,
    onSecondaryContainer = BlackPrimary,

    tertiary = BlackPrimary,
    onTertiary = WhitePrimary,
    tertiaryContainer = BlackSoft,
    onTertiaryContainer = WhitePrimary,

    background = WhitePrimary,
    onBackground = BlackPrimary,

    surface = WhiteMuted,
    onSurface = BlackPrimary,

    error = ErrorRed,
    onError = WhitePrimary
)


// ================================
//        THEME WRAPPER
// ================================
@Composable
fun P7ModernUiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
