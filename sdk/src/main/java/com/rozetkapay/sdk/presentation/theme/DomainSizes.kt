package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainSizes(
    val sheetCornerRadiusDp: Int,
    val componentCornerRadiusDp: Int,
    val buttonCornerRadiusDp: Int,
    val borderWidthDp: Int,
    val buttonHeightDp: Int,
    val googlePayButtonHeightDp: Int,
    val inputHeightDp: Int,
    val mainButtonTopPaddingDp: Int,
) : Parcelable {

    @IgnoredOnParcel
    val sheetCornerRadius: Dp = sheetCornerRadiusDp.dp

    @IgnoredOnParcel
    val componentCornerRadius: Dp = componentCornerRadiusDp.dp

    @IgnoredOnParcel
    val buttonCornerRadius: Dp = buttonCornerRadiusDp.dp

    @IgnoredOnParcel
    val borderWidth: Dp = borderWidthDp.dp

    @IgnoredOnParcel
    val buttonHeight: Dp = buttonHeightDp.dp

    @IgnoredOnParcel
    val googlePayButtonHeight: Dp = googlePayButtonHeightDp.dp

    @IgnoredOnParcel
    val inputHeight: Dp = inputHeightDp.dp

    @IgnoredOnParcel
    val mainButtonTopPadding: Dp = mainButtonTopPaddingDp.dp

    constructor(
        sheetCornerRadius: Dp,
        componentCornerRadius: Dp,
        buttonCornerRadius: Dp,
        borderWidth: Dp,
        buttonHeight: Dp,
        googlePayButtonHeight: Dp,
        inputHeight: Dp,
        mainButtonTopPadding: Dp,
    ) : this(
        sheetCornerRadiusDp = sheetCornerRadius.value.toInt(),
        componentCornerRadiusDp = componentCornerRadius.value.toInt(),
        buttonCornerRadiusDp = buttonCornerRadius.value.toInt(),
        borderWidthDp = borderWidth.value.toInt(),
        buttonHeightDp = buttonHeight.value.toInt(),
        googlePayButtonHeightDp = googlePayButtonHeight.value.toInt(),
        inputHeightDp = inputHeight.value.toInt(),
        mainButtonTopPaddingDp = mainButtonTopPadding.value.toInt(),
    )
}
