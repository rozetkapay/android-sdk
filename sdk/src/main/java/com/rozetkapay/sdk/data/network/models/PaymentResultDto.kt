package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentResultDto(
    @SerialName("action")
    val action: PaymentResultActionDto?,
    @SerialName("details")
    val details: PaymentResultDetailsDto,
    @SerialName("receipt_url")
    val receiptUrl: String?,
)

@Serializable
internal data class PaymentResultActionDto(
    @SerialName("type")
    val type: String?,
    @SerialName("value")
    val value: String?,
)

@Serializable
internal data class PaymentResultDetailsDto(
    @SerialName("payment_id")
    val paymentId: String,
    @SerialName("status")
    val status: String,
    @SerialName("status_code")
    val statusCode: String?,
    @SerialName("status_description")
    val statusDescription: String?,
)