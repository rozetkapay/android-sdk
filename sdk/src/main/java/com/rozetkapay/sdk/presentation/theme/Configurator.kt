package com.rozetkapay.sdk.presentation.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RozetkaPayThemeConfigurator(
    val lightColorScheme: DomainColorScheme = RozetkaPayDomainThemeDefaults.lightColors(),
    val darkColorScheme: DomainColorScheme = RozetkaPayDomainThemeDefaults.darkColors(),
) : Parcelable
