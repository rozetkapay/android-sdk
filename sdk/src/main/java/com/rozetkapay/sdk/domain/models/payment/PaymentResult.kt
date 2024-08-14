package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PaymentResult : Parcelable {

    @Parcelize
    data class Complete(
        val orderId: String,
        val paymentId: String,
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