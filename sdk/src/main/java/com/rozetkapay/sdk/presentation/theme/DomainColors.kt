package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainColorScheme(
    val surfaceInt: Int,
    val onSurfaceInt: Int,
    val appBarIconInt: Int,
    val titleInt: Int,
    val subtitleInt: Int,
    val errorInt: Int,
    val primaryInt: Int,
    val onPrimaryInt: Int,
    val placeholderInt: Int,
    val componentSurfaceInt: Int,
    val componentDividerInt: Int,
    val onComponentInt: Int,
) : Parcelable {

    @IgnoredOnParcel
    val surface: Color = Color(surfaceInt)

    @IgnoredOnParcel
    val onSurface: Color = Color(onSurfaceInt)

    @IgnoredOnParcel
    val appBarIcon: Color = Color(appBarIconInt)

    @IgnoredOnParcel
    val title: Color = Color(titleInt)

    @IgnoredOnParcel
    val subtitle: Color = Color(subtitleInt)

    @IgnoredOnParcel
    val error: Color = Color(errorInt)

    @IgnoredOnParcel
    val primary: Color = Color(primaryInt)

    @IgnoredOnParcel
    val onPrimary: Color = Color(onPrimaryInt)

    @IgnoredOnParcel
    val placeholder: Color = Color(placeholderInt)

    @IgnoredOnParcel
    val componentSurface: Color = Color(componentSurfaceInt)

    @IgnoredOnParcel
    val componentDivider: Color = Color(componentDividerInt)

    @IgnoredOnParcel
    val onComponent: Color = Color(onComponentInt)

    constructor(
        surface: Color,
        onSurface: Color,
        appBarIcon: Color,
        title: Color,
        subtitle: Color,
        error: Color,
        primary: Color,
        onPrimary: Color,
        placeholder: Color,
        componentSurface: Color,
        componentDivider: Color,
        onComponent: Color,
    ) : this(
        surfaceInt = surface.toArgb(),
        onSurfaceInt = onSurface.toArgb(),
        appBarIconInt = appBarIcon.toArgb(),
        titleInt = title.toArgb(),
        subtitleInt = subtitle.toArgb(),
        errorInt = error.toArgb(),
        primaryInt = primary.toArgb(),
        onPrimaryInt = onPrimary.toArgb(),
        placeholderInt = placeholder.toArgb(),
        componentSurfaceInt = componentSurface.toArgb(),
        componentDividerInt = componentDivider.toArgb(),
        onComponentInt = onComponent.toArgb(),
    )
}
