package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.data.network.converters.toCreatePaymentData
import com.rozetkapay.sdk.data.network.converters.toPaymentRequestDto
import com.rozetkapay.sdk.data.network.models.PaymentErrorDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.domain.errors.RozetkaPayNetworkException
import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest
import com.rozetkapay.sdk.domain.repository.PaymentsRepository
import com.rozetkapay.sdk.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ApiPaymentsRepository(
    private val apiProvider: ApiProvider,
    private val httpClient: HttpClient,
) : PaymentsRepository {

    override suspend fun createPayment(
        paymentRequest: PaymentRequest,
    ): CreatePaymentData = withContext(Dispatchers.IO) {
        when (paymentRequest) {
            is GooglePayPaymentRequest -> {
                createGooglePayPayment(paymentRequest)
            }
        }
    }

    private suspend fun createGooglePayPayment(paymentRequest: GooglePayPaymentRequest): CreatePaymentData {
        return createPayment(
            body = paymentRequest.toPaymentRequestDto(),
            authParameters = paymentRequest.authParameters
        )
    }

    private suspend fun createPayment(
        authParameters: ClientAuthParameters,
        body: Any,
    ): CreatePaymentData {
        Logger.d { "Create payment API request start" }
        val response = httpClient.post(
            urlString = apiProvider.createPaymentUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Create payment API request success" }
            val result = response.body<PaymentResultDto>()
            result.toCreatePaymentData()
        } else {
            Logger.d { "Create payment API request error" }
            val errorData = response.body<PaymentErrorDto?>()
            if (errorData != null) {
                throw RozetkaPayPaymentException(
                    code = errorData.code,
                    errorMessage = errorData.message,
                    type = errorData.type,
                )
            } else {
                val bodyString = response.bodyAsText()
                throw RozetkaPayNetworkException(
                    message = "Unknown error\n" +
                        "\tstatus code = ${response.status.value}\n" +
                        "\tbody = $bodyString"
                )
            }
        }
    }
}