package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainColorScheme(
    val surfaceInt: Int,
) : Parcelable {

    @IgnoredOnParcel
    val surface: Color = Color(surfaceInt)

    constructor(
        surface: Color,
    ) : this(
        surfaceInt = surface.toArgb()
    )
}

object RozetkaPayDomainThemeDefaults {
    fun lightColors(
        surface: Color = Color(0xFFFFFFFF),
    ) = DomainColorScheme(
        surface = surface
    )

    fun darkColors(
        surface: Color = Color(0xFF141313),
    ) = DomainColorScheme(
        surface = surface
    )
}

internal object DomainTheme {
    val colorScheme: DomainColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalDomainColorScheme.current
}

internal val LocalDomainColorScheme = staticCompositionLocalOf { RozetkaPayDomainThemeDefaults.lightColors() }

@Composable
fun DomainTheme(
    darkTheme: Boolean,
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) themeConfigurator.darkColorScheme else themeConfigurator.lightColorScheme
    CompositionLocalProvider(
        LocalDomainColorScheme provides colorScheme,
    ) {
        content()
    }
}
