package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BatchPaymentResultDto(
    @SerialName("action")
    val action: PaymentResultActionDto?,
    @SerialName("receipt_url")
    val receiptUrl: String?,
    @SerialName("orders_details")
    val ordersDetails: List<BatchPaymentOrderResultDetailsDto>,
)

@Serializable
internal data class BatchPaymentOrderResultDetailsDto(
    @SerialName("external_id")
    val externalId: String,
    @SerialName("operation_id")
    val operationId: String,
    @SerialName("transaction_id")
    val transactionId: String,
    @SerialName("status")
    val status: String,
    @SerialName("status_code")
    val statusCode: String?,
    @SerialName("status_description")
    val statusDescription: String?,
)

@Serializable
internal data class BatchPaymentStatusDto(
    @SerialName("batch_external_id")
    val externalId: String,
    @SerialName("status")
    val status: String,
    @SerialName("status_code")
    val statusCode: String?,
    @SerialName("status_description")
    val statusDescription: String?,
)