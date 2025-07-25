package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentRequestDto(
    @SerialName("amount")
    val amount: Double,
    @SerialName("currency")
    val currency: String,
    @SerialName("external_id")
    val externalId: String,
    @SerialName("callback_url")
    val callbackUrl: String? = null,
    @SerialName("result_url")
    val resultUrl: String? = null,
    @SerialName("mode")
    val mode: String = PaymentApiConstants.MODE_DIRECT,
    @SerialName("customer")
    val customer: CustomerDto,
)