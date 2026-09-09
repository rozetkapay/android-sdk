package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.BatchPaymentStatusDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDetailsDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.init.RozetkaPayLanguage
import com.rozetkapay.sdk.util.Logger

internal fun PaymentResultDto.toCreatePaymentData(
    language: RozetkaPayLanguage,
): CreatePaymentData = CreatePaymentData(
    action = this.action?.toAction(),
    paymentId = this.details.paymentId,
    status = this.details.status.toStatus(),
    statusCode = this.details.statusCode,
    statusDescription = this.details.resolveDescription(language),
)

internal fun PaymentResultDetailsDto.toCheckPaymentData(
    language: RozetkaPayLanguage,
): CheckPaymentData = CheckPaymentData(
    status = this.status.toStatus(),
    statusCode = this.statusCode,
    statusDescription = this.resolveDescription(language),
)

internal fun BatchPaymentStatusDto.toCheckPaymentData(
    language: RozetkaPayLanguage,
): CheckPaymentData =
    CheckPaymentData(
        status = this.status.toStatus(),
        statusCode = this.statusCode,
        statusDescription = this.resolveDescription(language),
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

