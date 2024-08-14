package com.rozetkapay.sdk.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientWidgetParameters(
    val widgetKey: String,
    val secretKey: String,
) : Parcelable
