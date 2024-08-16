package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.data.network.converters.toCheckPaymentData
import com.rozetkapay.sdk.data.network.converters.toCreatePaymentData
import com.rozetkapay.sdk.data.network.converters.toPaymentRequestDto
import com.rozetkapay.sdk.data.network.models.CardTokenDto
import com.rozetkapay.sdk.data.network.models.PaymentErrorDto
import com.rozetkapay.sdk.data.network.models.PaymentMethodDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.data.network.models.PurchaseDetailsDto
import com.rozetkapay.sdk.domain.errors.RozetkaPayNetworkException
import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentRequest
import com.rozetkapay.sdk.domain.repository.PaymentsRepository
import com.rozetkapay.sdk.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
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
        // TODO: restore real implementation
        return createFakeGooglePayRequest(paymentRequest)
        // return createPayment(
        //     body = paymentRequest.toPaymentRequestDto(),
        //     authParameters = paymentRequest.authParameters
        // )
    }

    private suspend fun createFakeGooglePayRequest(paymentRequest: GooglePayPaymentRequest): CreatePaymentData {
        val gPayBody = paymentRequest.toPaymentRequestDto()
        return createPayment(
            authParameters = paymentRequest.authParameters,
            body = gPayBody.copy(
                customer = gPayBody.customer.copy(
                    paymentMethod = PaymentMethodDto.cardToken(
                        cardToken = CardTokenDto(
                            token = "NmYzMTBmMzE2NDhhNDM0Mjg4YjY1MTJmYTlmOTg3MjE6SjVFZGd0THFmYjZBODdKdzBJ"  // 3ds + success
                            // token = "N2NlYzI5NTQwYWJkNGU2ZGI2MzRkZWQ1ZjgxYjBjZTg6cGRTM2d4bDkxbWI1bHBLeTdS"  // no 3ds + success
                            // token = "Mzg0ZmVmMTM1OGJhNGI0NjkzZTFjYzg1NzJiNWU4MTI6UWVOeFd5bzZlZjhnQjFEU21H" // 3ds + error
                            // token = "M2I2NzIyNWZhMGQ0NDVjMmFjZGFhZTg1NmNmMjkwZDE6cEpmTHJ2N2k4RW9WNWdlMGN2" // no 3ds + error
                        )
                    )
                )
            )
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
            response.handlePaymentApiError()
        }
    }

    override suspend fun checkPayment(
        authParameters: ClientAuthParameters,
        paymentId: String,
        orderId: String,
    ): CheckPaymentData {
        Logger.d { "Check payment with external id (orderI)=$orderId status start" }
        val response: HttpResponse = httpClient.get(
            urlString = apiProvider.paymentInfoUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            parameter("external_id", orderId)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Check payment API request success" }
            val result = response.body<PurchaseDetailsDto>()
            val details = result.details.firstOrNull { it.paymentId == paymentId }
            details?.toCheckPaymentData() ?: throw RozetkaPayPaymentException(
                code = "failure",
                errorMessage = "Payment with id $paymentId not found in purchase details of order $orderId",
            )
        } else {
            Logger.d { "Check payment API request error" }
            response.handlePaymentApiError()
        }
    }

    private suspend fun HttpResponse.handlePaymentApiError(): Nothing {
        val errorData = this.body<PaymentErrorDto?>()
        if (errorData != null) {
            throw RozetkaPayPaymentException(
                code = errorData.code,
                errorMessage = errorData.message,
                type = errorData.type,
            )
        } else {
            val bodyString = this.bodyAsText()
            throw RozetkaPayNetworkException(
                message = "Unknown error\n" +
                    "\tstatus code = ${this.status.value}\n" +
                    "\tbody = $bodyString"
            )
        }
    }
}