package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BatchPaymentResultDto(
    @SerialName("action")
    val action: PaymentResultActionDto?,
    @SerialName("receipt_url")
    val receiptUrl: String?,
    @SerialName("batch_details")
    val details: BatchPaymentResultDetailsDto,
    @SerialName("orders_details")
    val ordersDetails: List<BatchPaymentOrderResultDetailsDto>,
)

@Serializable
internal data class BatchPaymentResultDetailsDto(
    @SerialName("amount")
    val amount: Long,
    @SerialName("currency")
    val currency: String,
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