package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CustomerDto(
    @SerialName("payment_method")
    val paymentMethod: PaymentMethodDto,
)