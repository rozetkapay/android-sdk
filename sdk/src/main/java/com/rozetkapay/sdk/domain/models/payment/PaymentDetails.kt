package com.rozetkapay.sdk.domain.models.payment

internal data class PaymentDetails(
    val amount: Long,
    val currencyCode: String,
    val externalId: String,
    val callbackUrl: String? = null,
    val resultUrl: String? = null,
)