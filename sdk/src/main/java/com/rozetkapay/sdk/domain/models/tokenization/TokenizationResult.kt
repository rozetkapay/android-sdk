package com.rozetkapay.sdk.domain.models.tokenization

import android.os.Message
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class TokenizationResult : Parcelable {
    @Parcelize
    data class Complete(
        val tokenizedCard: TokenizedCard,
    ) : TokenizationResult()

    @Parcelize
    data class Failed(
        val message: String? = null,
        val error: Throwable? = null,
    ) : TokenizationResult()

    @Parcelize
    data object Cancelled : TokenizationResult()
}
