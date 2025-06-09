package com.rozetkapay.sdk.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientAuthParameters(
    val token: String,
    val widgetKey: String,
) : Parcelable
