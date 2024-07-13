package com.rozetkapay.sdk.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

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
internal fun DomainTheme(
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
