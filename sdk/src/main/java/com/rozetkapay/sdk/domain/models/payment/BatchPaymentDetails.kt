package com.rozetkapay.sdk.domain.models.payment

internal data class BatchPaymentDetails(
    val currencyCode: String,
    val externalId: String,
    val callbackUrl: String? = null,
    val resultUrl: String? = null,
    val orders: List<Order>,
) {

    data class Order(
        val apiKey: String,
        val amount: Long,
        val externalId: String,
        val description: String,
    )
}