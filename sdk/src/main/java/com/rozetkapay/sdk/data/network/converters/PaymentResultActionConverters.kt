package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.PaymentResultActionDto
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentAction

internal fun PaymentResultActionDto.toAction(): CreatePaymentAction {
    return when (this.type) {
        "url" -> CreatePaymentAction.Confirm3Ds(
            url = this.value!!
        )

        else -> CreatePaymentAction.Undefined(
            name = this.type,
            value = this.value
        )
    }
}