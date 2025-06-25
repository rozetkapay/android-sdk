package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.CardTokenDto
import com.rozetkapay.sdk.data.network.models.CustomerDto
import com.rozetkapay.sdk.data.network.models.GooglePayDto
import com.rozetkapay.sdk.data.network.models.PaymentMethodDto
import com.rozetkapay.sdk.domain.models.payment.CardTokenPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest

internal fun <T : Any> GooglePayPaymentRequest<T>.toCustomerDto() = CustomerDto(
    paymentMethod = PaymentMethodDto.googlePay(
        googlePay = GooglePayDto(
            token = this.googlePayToken
        )
    )
)

internal fun <T : Any> CardTokenPaymentRequest<T>.toCustomerDto(): CustomerDto = CustomerDto(
    paymentMethod = PaymentMethodDto.cardToken(
        cardToken = CardTokenDto(
            token = this.cardToken
        )
    )
)
