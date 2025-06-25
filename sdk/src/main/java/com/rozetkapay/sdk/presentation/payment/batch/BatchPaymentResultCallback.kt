package com.rozetkapay.sdk.presentation.payment.batch

import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult

fun interface BatchPaymentResultCallback {
    fun onPaymentResult(result: BatchPaymentResult)
}
