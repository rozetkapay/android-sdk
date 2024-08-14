package com.rozetkapay.sdk.domain.models.payment

import com.rozetkapay.sdk.domain.models.ClientPayParameters

internal sealed interface PaymentRequest {
    val clientParameters: ClientPayParameters
    val paymentParameters: BasePaymentParameters
}

internal data class BasePaymentParameters(
    val amount: Long,
    val currencyCode: String,
    val orderId: String,
)

internal data class GooglePayPaymentRequest(
    override val clientParameters: ClientPayParameters,
    override val paymentParameters: BasePaymentParameters,
    val token: String,
) : PaymentRequest