package com.rozetkapay.sdk.domain.models.payment

internal sealed class CreatePaymentResult {
    data class Error(
        val message: String? = null,
        val error: Throwable? = null,
    ) : CreatePaymentResult()

    data class Success(
        val paymentId: String,
    ) : CreatePaymentResult()

    data class Confirmation3DsRequired(
        val paymentId: String,
        val url: String,
    ) : CreatePaymentResult()
}