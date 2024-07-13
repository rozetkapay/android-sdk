package com.rozetkapay.sdk.domain.models.tokenization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenizationParameters(
    val requestTokenName: Boolean = false,
) : Parcelable
