package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed class ConfirmPaymentResult : Parcelable {

    @Parcelize
    data class Error(
        val paymentId: String,
        val message: String? = null,
        val error: Throwable? = null,
    ) : ConfirmPaymentResult()

    @Parcelize
    data class Success(
        val paymentId: String,
    ) : ConfirmPaymentResult()

    @Parcelize
    data object Cancelled : ConfirmPaymentResult()
}