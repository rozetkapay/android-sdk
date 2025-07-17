package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainTypography(
    val fontFamily: DomainTypography.FontFamily,
    val titleTextStyle: DomainTextStyle,
    val subtitleTextStyle: DomainTextStyle,
    val bodyTextStyle: DomainTextStyle,
    val labelSmallTextStyle: DomainTextStyle,
    val labelLargeTextStyle: DomainTextStyle,
    val inputTextStyle: DomainTextStyle,
    val legalTextTextStyle: DomainTextStyle,
) : Parcelable {

    @IgnoredOnParcel
    val fontFamilyValue: androidx.compose.ui.text.font.FontFamily = fontFamily.toFontFamily()

    @IgnoredOnParcel
    val title: TextStyle = titleTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val subtitle: TextStyle = subtitleTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val body: TextStyle = bodyTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val labelSmall: TextStyle = labelSmallTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val labelLarge: TextStyle = labelLargeTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val input: TextStyle = inputTextStyle.toTextStyle(fontFamilyValue)

    @IgnoredOnParcel
    val legalText: TextStyle = legalTextTextStyle.toTextStyle(fontFamilyValue)

    enum class FontFamily {
        Default,
        SansSerif,
        Serif,
        Monospace,
        Cursive,
    }
}

@Parcelize
data class DomainTextStyle(
    val fontSizeSp: Int,
    val lineHeightSp: Int,
    val fontWeight: DomainTextStyle.FontWeight,
) : Parcelable {

    enum class FontWeight() {
        Thin,
        ExtraLight,
        Light,
        Normal,
        Medium,
        SemiBold,
        Bold,
        ExtraBold,
        Black,
    }
}

internal fun DomainTextStyle.toTextStyle(
    fontFamily: FontFamily,
): TextStyle {
    return TextStyle(
        fontSize = fontSizeSp.sp,
        lineHeight = lineHeightSp.sp,
        fontWeight = when (fontWeight) {
            DomainTextStyle.FontWeight.Thin -> FontWeight.Thin
            DomainTextStyle.FontWeight.ExtraLight -> FontWeight.ExtraLight
            DomainTextStyle.FontWeight.Light -> FontWeight.Light
            DomainTextStyle.FontWeight.Normal -> FontWeight.Normal
            DomainTextStyle.FontWeight.Medium -> FontWeight.Medium
            DomainTextStyle.FontWeight.SemiBold -> FontWeight.SemiBold
            DomainTextStyle.FontWeight.Bold -> FontWeight.Bold
            DomainTextStyle.FontWeight.ExtraBold -> FontWeight.ExtraBold
            DomainTextStyle.FontWeight.Black -> FontWeight.Black
        },
        fontFamily = fontFamily
    )
}

internal fun DomainTypography.FontFamily.toFontFamily(): FontFamily {
    return when (this) {
        DomainTypography.FontFamily.Default -> FontFamily.Default
        DomainTypography.FontFamily.SansSerif -> FontFamily.SansSerif
        DomainTypography.FontFamily.Serif -> FontFamily.Serif
        DomainTypography.FontFamily.Monospace -> FontFamily.Monospace
        DomainTypography.FontFamily.Cursive -> FontFamily.Cursive
    }
}