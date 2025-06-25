package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.BatchPaymentOrderResultDetailsDto
import com.rozetkapay.sdk.data.network.models.BatchPaymentResultDto
import com.rozetkapay.sdk.domain.models.payment.BatchOrderPaymentResult
import com.rozetkapay.sdk.domain.models.payment.CreateBatchPaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.util.Logger

internal fun BatchPaymentResultDto.toCreateBatchPaymentData(): CreateBatchPaymentData {
    val firstOrder = this.ordersDetails.first()
    return CreateBatchPaymentData(
        action = this.action?.toAction(),
        status = firstOrder.status.toStatus(),
        statusCode = firstOrder.statusCode,
        statusDescription = firstOrder.statusDescription,
        ordersPayments = this.ordersDetails.map { it.toBatchOrderPaymentResult() }
    )
}

internal fun BatchPaymentOrderResultDetailsDto.toBatchOrderPaymentResult(): BatchOrderPaymentResult =
    BatchOrderPaymentResult(
        externalId = this.externalId,
        operationId = this.operationId
    )

private fun String.toStatus(): PaymentStatus {
    return when (this.lowercase()) {
        "init" -> PaymentStatus.Init
        "pending" -> PaymentStatus.Pending
        "success" -> PaymentStatus.Success
        "failure" -> PaymentStatus.Failure
        else -> {
            Logger.e { "Unknown payment status: $this" }
            PaymentStatus.Failure
        }
    }
}
