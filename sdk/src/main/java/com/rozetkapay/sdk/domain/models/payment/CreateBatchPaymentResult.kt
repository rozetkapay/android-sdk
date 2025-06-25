package com.rozetkapay.sdk.domain.models.payment

internal sealed class CreateBatchPaymentResult {
    data class Error(
        val message: String? = null,
        val error: Throwable? = null,
    ) : CreateBatchPaymentResult()

    data class Success(
        val ordersPayments: List<BatchOrderPaymentResult>,
    ) : CreateBatchPaymentResult()

    data class Confirmation3DsRequired(
        val ordersPayments: List<BatchOrderPaymentResult>,
        val url: String,
    ) : CreateBatchPaymentResult()
}