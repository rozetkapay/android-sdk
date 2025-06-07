package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import kotlinx.parcelize.Parcelize

sealed class BatchPaymentResult : Parcelable {

    @Parcelize
    data class Pending(
        val externalId: String,
        val ordersPayments: List<BatchOrderPaymentResult>,
    ) : BatchPaymentResult()

    @Parcelize
    data class Complete(
        val externalId: String,
        val ordersPayments: List<BatchOrderPaymentResult>,
        val tokenizedCard: TokenizedCard?,
    ) : BatchPaymentResult()

    @Parcelize
    data class Failed(
        val ordersPayments: List<BatchOrderPaymentResult>? = null,
        val message: String? = null,
        val error: Throwable? = null,
    ) : BatchPaymentResult()

    @Parcelize
    data object Cancelled : BatchPaymentResult()
}

