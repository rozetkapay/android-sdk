package com.rozetkapay.demo.domain.models

data class TokenizedCard(
    val token: String,
    val name: String,
    val maskedNumber: String,
    val paymentSystem: PaymentSystem,
)
