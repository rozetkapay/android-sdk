package com.rozetkapay.demo.domain.models

sealed class PaymentSystem {
    data object Visa : PaymentSystem()
    data object MasterCard : PaymentSystem()
    data object Prostir : PaymentSystem()
    data class Other(val name: String) : PaymentSystem()

}

fun String?.parsePaymentSystem(): PaymentSystem {
    return when (this?.lowercase()?.trim()) {
        PaymentSystem.Visa.toString().lowercase() -> PaymentSystem.Visa
        PaymentSystem.MasterCard.toString().lowercase() -> PaymentSystem.MasterCard
        PaymentSystem.Prostir.toString().lowercase() -> PaymentSystem.Prostir
        null -> PaymentSystem.Other("Unknown")
        else -> PaymentSystem.Other(this)
    }
}


