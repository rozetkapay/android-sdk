package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.domain.repository.PaymentsRepository
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

internal class CheckPaymentStatusUseCase(
    private val paymentsRepository: PaymentsRepository,
    private val retryTimeoutMs: Long = DEFAULT_RETRY_TIMEOUT_MS,
) : ResultUseCase<CheckPaymentStatusUseCase.Parameters, PaymentResult>() {

    override suspend fun doWork(params: Parameters): PaymentResult {
        var checkPaymentData = checkPaymentData(params)
        withTimeoutOrNull(retryTimeoutMs) {
            while (!checkPaymentData.isTerminatedStatus()) {
                delay(DEFAULT_RETRY_DELAY_MS)
                Logger.d { "Payment has not been terminated yet, retrying after $DEFAULT_RETRY_DELAY_MS ms delay" }
                checkPaymentData = checkPaymentData(params)
            }
        }
        return checkPaymentData.toPaymentResult(params)
    }

    private suspend fun checkPaymentData(params: Parameters): CheckPaymentData {
        return paymentsRepository.checkPayment(
            authParameters = params.authParameters,
            paymentId = params.paymentId,
            orderId = params.orderId
        )
    }

    private fun CheckPaymentData.isTerminatedStatus(): Boolean {
        return status == PaymentStatus.Success || status == PaymentStatus.Failure
    }

    private fun CheckPaymentData.toPaymentResult(
        params: Parameters,
    ): PaymentResult {
        return when (this.status) {
            PaymentStatus.Success -> {
                PaymentResult.Complete(
                    paymentId = params.paymentId,
                    orderId = params.orderId,
                )
            }

            PaymentStatus.Failure -> {
                PaymentResult.Failed(
                    paymentId = params.paymentId,
                    message = this.statusDescription,
                    error = RozetkaPayPaymentException(
                        code = "failure",
                        type = this.statusCode,
                        errorMessage = this.statusDescription ?: ""
                    )
                )
            }

            PaymentStatus.Init,
            PaymentStatus.Pending,
            -> {
                PaymentResult.Pending(
                    paymentId = params.paymentId,
                    orderId = params.orderId,
                )
            }
        }
    }

    data class Parameters(
        val authParameters: ClientAuthParameters,
        val paymentId: String,
        val orderId: String,
    )

    companion object {
        const val DEFAULT_RETRY_TIMEOUT_MS = 30_000L
        const val DEFAULT_RETRY_DELAY_MS = 1000L
    }
}