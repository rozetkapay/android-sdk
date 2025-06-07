package com.rozetkapay.sdk.domain.repository

import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentDetails
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreateBatchPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentDetails
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest

internal interface PaymentsRepository {

    suspend fun createPayment(
        paymentRequest: PaymentRequest<PaymentDetails>,
    ): CreatePaymentData

    suspend fun createBatchPayment(
        paymentRequest: PaymentRequest<BatchPaymentDetails>,
    ): CreateBatchPaymentData

    suspend fun checkPayment(
        authParameters: ClientAuthParameters,
        paymentId: String?,
        externalId: String,
    ): CheckPaymentData

    suspend fun checkBatchPayment(
        authParameters: ClientAuthParameters,
        externalId: String,
    ): CheckPaymentData
}