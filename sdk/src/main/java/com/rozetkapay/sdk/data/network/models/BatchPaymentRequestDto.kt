package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BatchPaymentRequestDto(
    @SerialName("currency")
    val currency: String,
    @SerialName("batch_external_id")
    val externalId: String,
    @SerialName("callback_url")
    val callbackUrl: String? = null,
    @SerialName("result_url")
    val resultUrl: String? = null,
    @SerialName("mode")
    val mode: String = PaymentApiConstants.MODE_DIRECT,
    @SerialName("orders")
    val orders: List<BatchOrderDto>,
    @SerialName("customer")
    val customer: CustomerDto,
)

@Serializable
internal data class BatchOrderDto(
    @SerialName("api_key")
    val apiKey: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("external_id")
    val externalId: String,
    @SerialName("description")
    val description: String,
)