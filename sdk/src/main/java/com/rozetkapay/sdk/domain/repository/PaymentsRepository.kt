package com.rozetkapay.sdk.domain.repository

import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest

internal interface PaymentsRepository {

    suspend fun createPayment(
        paymentRequest: PaymentRequest,
    ): CreatePaymentData

    suspend fun checkPayment(
        authParameters: ClientAuthParameters,
        paymentId: String,
        orderId: String,
    ): CheckPaymentData
}