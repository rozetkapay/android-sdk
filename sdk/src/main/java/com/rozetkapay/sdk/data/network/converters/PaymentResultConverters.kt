package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.BatchPaymentStatusDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDetailsDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.util.Logger

internal fun PaymentResultDto.toCreatePaymentData(): CreatePaymentData = CreatePaymentData(
    action = this.action?.toAction(),
    paymentId = this.details.paymentId,
    status = this.details.status.toStatus(),
    statusCode = this.details.statusCode,
    statusDescription = this.details.statusDescription,
)

internal fun PaymentResultDetailsDto.toCheckPaymentData(): CheckPaymentData = CheckPaymentData(
    status = this.status.toStatus(),
    statusCode = this.statusCode,
    statusDescription = this.statusDescription,
)

internal fun BatchPaymentStatusDto.toCheckPaymentData(): CheckPaymentData = CheckPaymentData(
    status = this.status.toStatus(),
    statusCode = this.statusCode,
    statusDescription = this.statusDescription,
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

