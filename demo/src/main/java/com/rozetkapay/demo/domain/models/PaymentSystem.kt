package com.rozetkapay.demo.domain.models

sealed class PaymentSystem {
    data object Visa : PaymentSystem()
    data object MasterCard : PaymentSystem()
    data class Other(val name: String) : PaymentSystem()
}
