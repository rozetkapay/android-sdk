package com.rozetkapay.sdk.domain.models.payment

import com.rozetkapay.sdk.domain.models.ClientAuthParameters

internal sealed interface PaymentRequest {
    val authParameters: ClientAuthParameters
    val paymentParameters: BasePaymentParameters
}

internal data class BasePaymentParameters(
    val amount: Long,
    val currencyCode: String,
    val orderId: String,
    val callbackUrl: String? = null,
)

internal data class GooglePayPaymentRequest(
    override val authParameters: ClientAuthParameters,
    override val paymentParameters: BasePaymentParameters,
    val googlePayToken: String,
) : PaymentRequest