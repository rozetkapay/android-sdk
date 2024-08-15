package com.rozetkapay.sdk.domain.models.payment

internal data class CheckPaymentData(
    val paymentId: String,
    val status: PaymentStatus,
    val statusCode: String?,
    val statusDescription: String?,
)