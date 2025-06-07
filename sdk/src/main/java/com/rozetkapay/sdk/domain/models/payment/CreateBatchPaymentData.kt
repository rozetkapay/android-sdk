package com.rozetkapay.sdk.domain.models.payment

internal data class CreateBatchPaymentData(
    val action: CreatePaymentAction?,
    val ordersPayments: List<BatchOrderPaymentResult>,
    val status: PaymentStatus,
    val statusCode: String?,
    val statusDescription: String?,
)