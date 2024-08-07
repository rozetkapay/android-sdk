package com.rozetkapay.sdk.presentation.payment

import com.rozetkapay.sdk.domain.models.payment.PaymentResult

fun interface PaymentSheetResultCallback {
    fun onPaymentSheetResult(result: PaymentResult)
}
