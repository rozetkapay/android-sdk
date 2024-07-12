package com.rozetkapay.sdk.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object RozetkaPayThemeDefaults {
    val lightColors: ColorScheme = lightColorScheme(
        primary = Color(0xFF005D26),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF00873A),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFF3A6841),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFBFF4C1),
        onSecondaryContainer = Color(0xFF26542E),
        tertiary = Color(0xFF0049B3),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF326EE7),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFFA5000D),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFE12625),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFF161D16),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1C1B1B),
        surfaceVariant = Color(0xFFE0E0E0),
        onSurfaceVariant = Color(0xFF444748),
        outline = Color(0xFF747878),
        outlineVariant = Color(0xFFC4C7C8),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF313030),
        inverseOnSurface = Color(0xFFF4F0EF),
        inversePrimary = Color(0xFF5FDF7C),
    )
    val darkColors: ColorScheme = darkColorScheme(
        primary = Color(0xFF5FDF7C),
        onPrimary = Color(0xFF003914),
        primaryContainer = Color(0xFF00873A),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFFA0D3A3),
        onSecondary = Color(0xFF063916),
        secondaryContainer = Color(0xFF194824),
        onSecondaryContainer = Color(0xFFADE1AF),
        tertiary = Color(0xFFB1C5FF),
        onTertiary = Color(0xFF002C72),
        tertiaryContainer = Color(0xFF326EE7),
        onTertiaryContainer = Color(0xFFFFFFFF),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFFE12625),
        onErrorContainer = Color(0xFFFFFFFF),
        background = Color(0xFF0E150E),
        onBackground = Color(0xFFDDE5D9),
        surface = Color(0xFF141313),
        onSurface = Color(0xFFE5E2E1),
        surfaceVariant = Color(0xFF444748),
        onSurfaceVariant = Color(0xFFC4C7C8),
        outline = Color(0xFF8E9192),
        outlineVariant = Color(0xFF444748),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE5E2E1),
        inverseOnSurface = Color(0xFF313030),
        inversePrimary = Color(0xFF006E2E),
    )

    @Composable
    fun typography(): Typography {
        return MaterialTheme.typography
    }

    @Composable
    fun shapes(): Shapes {
        return MaterialTheme.shapes
    }
}

@Composable
internal fun RozetkaPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) RozetkaPayThemeDefaults.darkColors else RozetkaPayThemeDefaults.lightColors,
        shapes = RozetkaPayThemeDefaults.shapes(),
        typography = RozetkaPayThemeDefaults.typography(),
    ) {
        DomainTheme(
            darkTheme = darkTheme,
            themeConfigurator = themeConfigurator
        ) {
            content()
        }
    }
}
