package com.rozetkapay.demo.domain.models

data class CardToken(
    val token: String,
    val name: String,
    val maskedNumber: String,
    val paymentSystem: PaymentSystem,
)
