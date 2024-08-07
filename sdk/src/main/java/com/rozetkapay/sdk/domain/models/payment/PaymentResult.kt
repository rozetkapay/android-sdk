package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PaymentResult : Parcelable {
    @Parcelize
    data object Complete : PaymentResult()

    @Parcelize
    data class Failed(
        val message: String? = null,
        val error: Throwable? = null,
    ) : PaymentResult()

    @Parcelize
    data object Cancelled : PaymentResult()
}