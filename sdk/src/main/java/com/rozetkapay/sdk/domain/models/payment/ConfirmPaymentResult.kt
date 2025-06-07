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
    data object Success : ConfirmPaymentResult

    // used when confirmation completed but status is unknown
    // real payment status should be handled by client
    @Parcelize
    data object Completed : ConfirmPaymentResult

    @Parcelize
    data object Cancelled: ConfirmPaymentResult
}