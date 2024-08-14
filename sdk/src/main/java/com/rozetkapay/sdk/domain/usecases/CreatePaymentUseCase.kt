package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentResult
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest
import com.rozetkapay.sdk.domain.repository.PaymentsRepository

internal class CreatePaymentUseCase(
    private val paymentsRepository: PaymentsRepository,
) : ResultUseCase<PaymentRequest, CreatePaymentResult>() {

    override suspend fun doWork(
        params: PaymentRequest,
    ): CreatePaymentResult {
        return try {
            val data = paymentsRepository.createPayment(params)
            when (data.status) {
                CreatePaymentData.Status.Success -> {
                    CreatePaymentResult.Success(
                        paymentId = data.paymentId,
                    )
                }

                CreatePaymentData.Status.Failure -> {
                    CreatePaymentResult.Error(
                        message = data.statusDescription,
                        error = RozetkaPayPaymentException(
                            code = "failure",
                            type = data.statusCode,
                            errorMessage = data.statusDescription ?: ""
                        )
                    )
                }

                CreatePaymentData.Status.Init,
                CreatePaymentData.Status.Pending,
                -> {
                    if (data.action is CreatePaymentData.Action.Confirm3Ds) {
                        CreatePaymentResult.Confirmation3DsRequired(
                            paymentId = data.paymentId,
                            url = data.action.url,
                        )
                    } else {
                        CreatePaymentResult.Error(
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
            CreatePaymentResult.Error(error = e)
        }
    }
}