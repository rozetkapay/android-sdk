package com.rozetkapay.sdk.domain.models.payment

import com.rozetkapay.sdk.domain.models.ClientAuthParameters

internal sealed interface PaymentRequest<T: Any> {
    val authParameters: ClientAuthParameters
    val paymentDetails: T
}

internal data class GooglePayPaymentRequest<T: Any>(
    override val authParameters: ClientAuthParameters,
    override val paymentDetails: T,
    val googlePayToken: String,
) : PaymentRequest<T>

internal data class CardTokenPaymentRequest<T: Any>(
    override val authParameters: ClientAuthParameters,
    override val paymentDetails: T,
    val cardToken: String,
) : PaymentRequest<T>