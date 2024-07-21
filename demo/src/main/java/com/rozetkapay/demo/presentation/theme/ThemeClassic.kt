package com.rozetkapay.demo.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.presentation.theme.RozetkaPayDomainThemeDefaults
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

private val lightScheme = with(RozetkaPayDemoAppClassicColors) {
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight
    )
}

private val darkScheme = with(RozetkaPayDemoAppClassicColors) {
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )
}

@Composable
fun RozetkaPayDemoClassicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        typography = rozetkaPayDemoAppTypography(),
        content = content
    )
}

// TODO: create full customized classic UI theme for SDK
val classicRozetkaPaySdkThemeConfigurator = RozetkaPayThemeConfigurator(
    lightColorScheme = RozetkaPayDomainThemeDefaults.lightColors(
        surface = lightScheme.primaryContainer,
        primary = lightScheme.primary,
        onPrimary = lightScheme.onPrimary,
        appBarIcon = lightScheme.primary,
        componentDivider = lightScheme.primaryContainer,
        subtitle = lightScheme.secondary,
        componentSurface = lightScheme.surfaceContainerLow,
        onComponent = darkScheme.onSurface
    ),
    darkColorScheme = RozetkaPayDomainThemeDefaults.darkColors(
        surface = darkScheme.primaryContainer,
        primary = darkScheme.primary,
        onPrimary = darkScheme.onPrimary,
        appBarIcon = darkScheme.primary,
        componentDivider = darkScheme.primaryContainer,
        subtitle = darkScheme.secondary,
        componentSurface = darkScheme.surfaceContainerLow,
        onComponent = darkScheme.onSurfaceVariant
    ),
    sizes = RozetkaPayDomainThemeDefaults.sizes(
        sheetCornerRadius = 0.dp,
        componentCornerRadius = 0.dp,
        buttonCornerRadius = 50.dp,
        borderWidth = 8.dp
    ),
)
