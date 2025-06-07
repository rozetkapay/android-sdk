package com.rozetkapay.sdk.presentation.payment.regular

import com.rozetkapay.sdk.domain.models.payment.PaymentResult

fun interface PaymentResultCallback {
    fun onPaymentResult(result: PaymentResult)
}
