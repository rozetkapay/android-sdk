package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PurchaseDetailsDto(
    @SerialName("purchase_details")
    val details: List<PaymentResultDetailsDto>,
    @SerialName("receipt_url")
    val receiptUrl: String?,
)