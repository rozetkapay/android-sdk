package com.rozetkapay.sdk.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object RozetkaPayDomainThemeDefaults {
    fun lightColors(
        surface: Color = Color(0xFFFFFFFF),
        onSurface: Color = Color(0xFF2B2B2B),
        appBarIcon: Color = Color(0xFF9DA2A6),
        title: Color = Color(0xFF2B2B2B),
        subtitle: Color = Color(0xFF414345),
        error: Color = Color(0xFFFF0B0B),
        primary: Color = Color(0xFF00A046),
        onPrimary: Color = Color(0xFFFFFFFF),
        placeholder: Color = Color(0xFF9DA2A6),
        componentSurface: Color = Color(0xFFF6F7F9),
        componentDivider: Color = Color(0xFFDFE2E5),
        onComponent: Color = Color(0xFF2B2B2B),
    ) = DomainColorScheme(
        surface = surface,
        onSurface = onSurface,
        appBarIcon = appBarIcon,
        title = title,
        subtitle = subtitle,
        error = error,
        primary = primary,
        onPrimary = onPrimary,
        placeholder = placeholder,
        componentSurface = componentSurface,
        componentDivider = componentDivider,
        onComponent = onComponent,
    )

    fun darkColors(
        // TODO: add dark theme colors
        surface: Color = Color(0xFFFFFFFF),
        onSurface: Color = Color(0xFF2B2B2B),
        appBarIcon: Color = Color(0xFF9DA2A6),
        title: Color = Color(0xFF2B2B2B),
        subtitle: Color = Color(0xFF414345),
        error: Color = Color(0xFFFF0B0B),
        primary: Color = Color(0xFF00A046),
        onPrimary: Color = Color(0xFFFFFFFF),
        placeholder: Color = Color(0xFF9DA2A6),
        componentSurface: Color = Color(0xFFF6F7F9),
        componentDivider: Color = Color(0xFFDFE2E5),
        onComponent: Color = Color(0xFF2B2B2B),
    ) = DomainColorScheme(
        surface = surface,
        onSurface = onSurface,
        appBarIcon = appBarIcon,
        title = title,
        subtitle = subtitle,
        error = error,
        primary = primary,
        onPrimary = onPrimary,
        placeholder = placeholder,
        componentSurface = componentSurface,
        componentDivider = componentDivider,
        onComponent = onComponent,
    )

    fun sizes(
        sheetCornerRadius: Dp = 24.dp,
        componentCornerRadius: Dp = 12.dp,
        buttonCornerRadius: Dp = 12.dp,
        borderWidth: Dp = 1.dp,
    ) = DomainSizes(
        sheetCornerRadius = sheetCornerRadius,
        componentCornerRadius = componentCornerRadius,
        buttonCornerRadius = buttonCornerRadius,
        borderWidth = borderWidth,
    )
}

internal object DomainTheme {
    val colorScheme: DomainColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalDomainColorScheme.current
    val sizes: DomainSizes
        @Composable
        @ReadOnlyComposable
        get() = LocalDomainSizes.current
}

internal val LocalDomainColorScheme = staticCompositionLocalOf { RozetkaPayDomainThemeDefaults.lightColors() }
internal val LocalDomainSizes = staticCompositionLocalOf { RozetkaPayDomainThemeDefaults.sizes() }

@Composable
fun DomainTheme(
    darkTheme: Boolean,
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) themeConfigurator.darkColorScheme else themeConfigurator.lightColorScheme
    val sizes = themeConfigurator.sizes
    CompositionLocalProvider(
        LocalDomainColorScheme provides colorScheme,
        LocalDomainSizes provides sizes
    ) {
        content()
    }
}
