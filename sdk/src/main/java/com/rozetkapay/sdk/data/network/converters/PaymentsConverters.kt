package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.BatchOrderDto
import com.rozetkapay.sdk.data.network.models.BatchPaymentRequestDto
import com.rozetkapay.sdk.data.network.models.CustomerDto
import com.rozetkapay.sdk.data.network.models.PaymentRequestDto
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentDetails
import com.rozetkapay.sdk.domain.models.payment.PaymentDetails

internal fun PaymentDetails.toPaymentRequestDto(
    customer: CustomerDto,
): PaymentRequestDto = PaymentRequestDto(
    amount = this.amount.toAmountDto(),
    currency = this.currencyCode,
    externalId = this.externalId,
    callbackUrl = this.callbackUrl,
    resultUrl = this.resultUrl,
    customer = customer
)

internal fun BatchPaymentDetails.toBatchPaymentRequestDto(
    customer: CustomerDto,
): BatchPaymentRequestDto = BatchPaymentRequestDto(
    currency = this.currencyCode,
    externalId = this.externalId,
    callbackUrl = this.callbackUrl,
    resultUrl = this.resultUrl,
    orders = this.orders.map { it.toBatchOrderDto() },
    customer = customer
)

internal fun BatchPaymentDetails.Order.toBatchOrderDto(): BatchOrderDto = BatchOrderDto(
    apiKey = this.apiKey,
    amount = this.amount.toAmountDto(),
    externalId = this.externalId,
    description = this.description,
)