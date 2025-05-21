package com.rozetkapay.sdk.domain.models.tokenization

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenizationParameters(
    val cardFieldsParameters: CardFieldsParameters = CardFieldsParameters(),
) : Parcelable
