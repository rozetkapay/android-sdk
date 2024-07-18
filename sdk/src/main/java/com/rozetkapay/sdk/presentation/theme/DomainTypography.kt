package com.rozetkapay.sdk.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal interface DomainTypography {
    val title: TextStyle
    val subtitle: TextStyle
    val body: TextStyle
    val labelSmall: TextStyle
    val labelLarge: TextStyle
    val input: TextStyle
}

internal object DomainTypographyDefaults : DomainTypography {
    private val defaultStyle = TextStyle(
        fontFamily = FontFamily.SansSerif
    )

    override val title: TextStyle = defaultStyle.copy(
        fontSize = 22.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
    override val subtitle: TextStyle = defaultStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
    override val body: TextStyle = defaultStyle.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal
    )
    override val labelSmall: TextStyle = defaultStyle.copy(
        fontSize = 14.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal
    )
    override val labelLarge: TextStyle = defaultStyle.copy(
        fontSize = 18.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
    override val input: TextStyle = defaultStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal
    )
}
