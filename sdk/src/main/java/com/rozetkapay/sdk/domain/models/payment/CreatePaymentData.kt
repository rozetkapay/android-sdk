package com.rozetkapay.sdk.domain.models.payment

internal data class CreatePaymentData(
    val action: CreatePaymentAction?,
    val paymentId: String,
    val status: PaymentStatus,
    val statusCode: String?,
    val statusDescription: String?,
)