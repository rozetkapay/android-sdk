package com.rozetkapay.sdk.presentation.payment.batch

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult
import com.rozetkapay.sdk.domain.models.payment.PaymentResult

internal sealed interface BatchPaymentEvent {
    data class Result(
        val result: BatchPaymentResult,
    ) : BatchPaymentEvent

    data class StartGooglePayPayment(
        val task: Task<PaymentData>,
    ) : BatchPaymentEvent

    data class Start3dsConfirmation(
        val url: String,
    ) : BatchPaymentEvent
}