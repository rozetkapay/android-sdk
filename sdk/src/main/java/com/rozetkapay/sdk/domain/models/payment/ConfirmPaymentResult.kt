package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface ConfirmPaymentResult : Parcelable {

    @Parcelize
    data class Error(
        val message: String? = null,
        val error: Throwable? = null,
    ) : ConfirmPaymentResult

    @Parcelize
    data class Success(
        val paymentId: String,
    ) : ConfirmPaymentResult

    // used when confirmation completed bu status is unknown
    // real payment sta should be handled by client
    @Parcelize
    data class Completed(
        val paymentId: String,
    ) : ConfirmPaymentResult

    @Parcelize
    data class Cancelled(
        val paymentId: String,
    ) : ConfirmPaymentResult
}