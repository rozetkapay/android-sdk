package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PaymentResultActionDto(
    @SerialName("type")
    val type: String?,
    @SerialName("value")
    val value: String?,
)