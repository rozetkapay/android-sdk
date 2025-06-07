package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import kotlinx.parcelize.Parcelize

sealed class PaymentResult : Parcelable {

    @Parcelize
    data class Pending(
        val externalId: String,
        val paymentId: String,
    ) : PaymentResult()

    @Parcelize
    data class Complete(
        val externalId: String,
        val paymentId: String,
        val tokenizedCard: TokenizedCard?,
    ) : PaymentResult()

    @Parcelize
    data class Failed(
        val paymentId: String? = null,
        val message: String? = null,
        val error: Throwable? = null,
    ) : PaymentResult()

    @Parcelize
    data object Cancelled : PaymentResult()
}