package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.models.PaymentSystem

internal class ProvideCardPaymentSystemUseCase {

    fun invoke(cardNumberPrefix: String): PaymentSystem? {
        val prefix = cardNumberPrefix.filter { it.isDigit() }
        return PaymentSystem.entries.firstOrNull { paymentSystem ->
            paymentSystem.prefixes.any {
                it.hasEqualPrefix(prefix)
            }
        }
    }
}