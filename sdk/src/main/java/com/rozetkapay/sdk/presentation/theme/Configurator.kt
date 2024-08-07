package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize

@Parcelize
data class RozetkaPayThemeConfigurator(
    val lightColorScheme: DomainColorScheme = RozetkaPayDomainThemeDefaults.lightColors(),
    val darkColorScheme: DomainColorScheme = RozetkaPayDomainThemeDefaults.darkColors(),
    val sizes: DomainSizes = RozetkaPayDomainThemeDefaults.sizes(),
) : Parcelable

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
        surface: Color = Color(0xFF221F1F),
        onSurface: Color = Color(0xFFEEEEEE),
        appBarIcon: Color = Color(0xFFA7A5A5),
        title: Color = Color(0xFFEEEEEE),
        subtitle: Color = Color(0xFFA7A5A5),
        error: Color = Color(0xFFE56464),
        primary: Color = Color(0xFF00A046),
        onPrimary: Color = Color(0xFFFFFFFF),
        placeholder: Color = Color(0xFF9B9EA0),
        componentSurface: Color = Color(0xFF363436),
        componentDivider: Color = Color(0xFF4E4C4C),
        onComponent: Color = Color(0xFFEEEEEE),
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
        sheetCornerRadius: Dp = 20.dp,
        componentCornerRadius: Dp = 12.dp,
        buttonCornerRadius: Dp = 12.dp,
        borderWidth: Dp = 1.dp,
    ) = DomainSizes(
        sheetCornerRadius = sheetCornerRadius,
        componentCornerRadius = componentCornerRadius,
        buttonCornerRadius = buttonCornerRadius,
        borderWidth = borderWidth,
    )

    internal fun typography() = DomainTypographyDefaults
}
