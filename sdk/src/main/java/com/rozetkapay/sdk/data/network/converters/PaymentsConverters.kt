package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.CustomerDto
import com.rozetkapay.sdk.data.network.models.GooglePayDto
import com.rozetkapay.sdk.data.network.models.PaymentMethodDto
import com.rozetkapay.sdk.data.network.models.PaymentRequestDto
import com.rozetkapay.sdk.data.network.models.PaymentResultActionDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDetailsDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.util.Logger

internal fun GooglePayPaymentRequest.toPaymentRequestDto(): PaymentRequestDto = PaymentRequestDto(
    amount = this.paymentParameters.amount.toBigDecimal().divide(100.toBigDecimal()).toDouble(),
    currency = this.paymentParameters.currencyCode,
    externalId = this.paymentParameters.orderId,
    callbackUrl = this.paymentParameters.callbackUrl,
    customer = CustomerDto(
        paymentMethod = PaymentMethodDto.googlePay(
            googlePay = GooglePayDto(
                token = this.googlePayToken
            )
        )
    )
)

internal fun PaymentResultDto.toCreatePaymentData(): CreatePaymentData = CreatePaymentData(
    action = this.action?.toAction(),
    paymentId = this.details.paymentId,
    status = this.details.toStatus(),
    statusCode = this.details.statusCode,
    statusDescription = this.details.statusDescription,
)

private fun PaymentResultDetailsDto.toStatus(): CreatePaymentData.Status {
    return when (this.status) {
        "init" -> CreatePaymentData.Status.Init
        "pending" -> CreatePaymentData.Status.Pending
        "success" -> CreatePaymentData.Status.Success
        "failure" -> CreatePaymentData.Status.Failure
        else -> {
            Logger.e { "Unknown payment status: $this" }
            CreatePaymentData.Status.Failure
        }
    }
}

private fun PaymentResultActionDto.toAction(): CreatePaymentData.Action {
    return when (this.type) {
        "url" -> CreatePaymentData.Action.Confirm3Ds(
            url = this.value!!
        )

        else -> CreatePaymentData.Action.Undefined(
            name = this.type,
            value = this.value
        )
    }
}