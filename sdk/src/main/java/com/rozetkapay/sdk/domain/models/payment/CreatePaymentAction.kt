package com.rozetkapay.sdk.domain.models.payment

sealed class CreatePaymentAction {
    data class Undefined(
        val name: String?,
        val value: String?,
    ) : CreatePaymentAction()

    data class Confirm3Ds(
        val url: String,
    ) : CreatePaymentAction()
}