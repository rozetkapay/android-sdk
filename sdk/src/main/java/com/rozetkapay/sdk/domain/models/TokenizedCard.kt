package com.rozetkapay.sdk.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenizedCard(
    val token: String,
    val maskedNumber: String,
    val paymentSystem: String?,
    val name: String? = null,
) : Parcelable
