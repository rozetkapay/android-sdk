package com.rozetkapay.sdk.domain.models.payment

internal data class CreatePaymentData(
    val action: Action?,
    val paymentId: String,
    val status: Status,
    val statusCode: String?,
    val statusDescription: String?,
) {

    sealed class Action {
        data class Undefined(
            val name: String?,
            val value: String?,
        ) : Action()

        data class Confirm3Ds(
            val url: String,
        ) : Action()
    }

    enum class Status {
        Init,
        Pending,
        Success,
        Failure
    }
}