package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentDetails
import com.rozetkapay.sdk.domain.models.payment.CreateBatchPaymentResult
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentAction
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.domain.repository.PaymentsRepository

internal class CreateBatchPaymentUseCase(
    private val paymentsRepository: PaymentsRepository,
) : ResultUseCase<PaymentRequest<BatchPaymentDetails>, CreateBatchPaymentResult>() {

    override suspend fun doWork(
        params: PaymentRequest<BatchPaymentDetails>,
    ): CreateBatchPaymentResult {
        return try {
            val data = paymentsRepository.createBatchPayment(params)
            when (data.status) {
                PaymentStatus.Success -> {
                    CreateBatchPaymentResult.Success(
                        ordersPayments = data.ordersPayments
                    )
                }

                PaymentStatus.Failure -> {
                    CreateBatchPaymentResult.Error(
                        message = data.statusDescription,
                        error = RozetkaPayPaymentException(
                            code = "failure",
                            type = data.statusCode,
                            errorMessage = data.statusDescription ?: ""
                        )
                    )
                }

                PaymentStatus.Init,
                PaymentStatus.Pending,
                    -> {
                    if (data.action is CreatePaymentAction.Confirm3Ds) {
                        CreateBatchPaymentResult.Confirmation3DsRequired(
                            ordersPayments = data.ordersPayments,
                            url = data.action.url,
                        )
                    } else {
                        CreateBatchPaymentResult.Error(
                            message = "Unknown action",
                            error = RozetkaPayPaymentException(
                                code = "unknown_action",
                                type = "unknown_action",
                                errorMessage = "3DS confirmation action expected, but action is ${data.action}, " +
                                    "payment can't be finished"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            CreateBatchPaymentResult.Error(error = e)
        }
    }
}