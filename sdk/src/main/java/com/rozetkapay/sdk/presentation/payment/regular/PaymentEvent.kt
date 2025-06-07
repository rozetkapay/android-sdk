package com.rozetkapay.sdk.presentation.payment.regular

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentResult

internal sealed interface PaymentEvent {
    data class Result(
        val result: PaymentResult,
    ) : PaymentEvent

    data class StartGooglePayPayment(
        val task: Task<PaymentData>,
    ) : PaymentEvent

    data class Start3dsConfirmation(
        val url: String,
    ) : PaymentEvent
}