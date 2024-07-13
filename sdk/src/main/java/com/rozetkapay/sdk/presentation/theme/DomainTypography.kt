package com.rozetkapay.sdk.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal interface DomainTypography {
    val title: TextStyle
    val subtitle: TextStyle
    val label: TextStyle
}

internal object DomainTypographyDefaults : DomainTypography {
    private val defaultStyle = TextStyle(
        fontFamily = FontFamily.SansSerif
    )

    override val title: TextStyle = defaultStyle.copy(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
    override val subtitle: TextStyle = defaultStyle.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
    override val label: TextStyle = defaultStyle.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
}
