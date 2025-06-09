package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.data.network.converters.toBatchPaymentRequestDto
import com.rozetkapay.sdk.data.network.converters.toCheckPaymentData
import com.rozetkapay.sdk.data.network.converters.toCreateBatchPaymentData
import com.rozetkapay.sdk.data.network.converters.toCreatePaymentData
import com.rozetkapay.sdk.data.network.converters.toCustomerDto
import com.rozetkapay.sdk.data.network.converters.toPaymentRequestDto
import com.rozetkapay.sdk.data.network.models.BatchPaymentResultDto
import com.rozetkapay.sdk.data.network.models.BatchPaymentStatusDto
import com.rozetkapay.sdk.data.network.models.PaymentErrorDto
import com.rozetkapay.sdk.data.network.models.PaymentResultDto
import com.rozetkapay.sdk.data.network.models.PurchaseDetailsDto
import com.rozetkapay.sdk.domain.errors.RozetkaPayNetworkException
import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentDetails
import com.rozetkapay.sdk.domain.models.payment.CardTokenPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.CheckPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreateBatchPaymentData
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentData
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentDetails
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

    // regular payments

    override suspend fun createPayment(
        paymentRequest: PaymentRequest<PaymentDetails>,
    ): CreatePaymentData = withContext(Dispatchers.IO) {
        val customer = when (paymentRequest) {
            is GooglePayPaymentRequest -> paymentRequest.toCustomerDto()
            is CardTokenPaymentRequest -> paymentRequest.toCustomerDto()
        }
        createPayment(
            body = paymentRequest.paymentDetails.toPaymentRequestDto(
                customer = customer
            ),
            authParameters = paymentRequest.authParameters
        )
    }

    private suspend fun createPayment(
        authParameters: ClientAuthParameters,
        body: Any,
    ): CreatePaymentData {
        Logger.d { "Create payment - API request - start" }
        val response = httpClient.post(
            urlString = apiProvider.createPaymentUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Create payment - API request - success" }
            val result = response.body<PaymentResultDto>()
            result.toCreatePaymentData()
        } else {
            Logger.d { "Create payment - API request - error" }
            response.handlePaymentApiError()
        }
    }

    // batch payments

    override suspend fun createBatchPayment(
        paymentRequest: PaymentRequest<BatchPaymentDetails>,
    ): CreateBatchPaymentData = withContext(Dispatchers.IO) {
        val customer = when (paymentRequest) {
            is GooglePayPaymentRequest -> paymentRequest.toCustomerDto()
            is CardTokenPaymentRequest -> paymentRequest.toCustomerDto()
        }
        createBatchPayment(
            body = paymentRequest.paymentDetails.toBatchPaymentRequestDto(
                customer = customer
            ),
            authParameters = paymentRequest.authParameters
        )
    }

    private suspend fun createBatchPayment(
        authParameters: ClientAuthParameters,
        body: Any,
    ): CreateBatchPaymentData {
        Logger.d { "Create batch payment - API request - start" }
        val response = httpClient.post(
            urlString = apiProvider.createBatchPaymentUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Create batch payment - API request - success" }
            val result = response.body<BatchPaymentResultDto>()
            result.toCreateBatchPaymentData()
        } else {
            Logger.d { "Create batch payment - API request - error" }
            response.handlePaymentApiError()
        }
    }

    // check payment

    override suspend fun checkPayment(
        authParameters: ClientAuthParameters,
        paymentId: String?,
        externalId: String,
    ): CheckPaymentData {
        Logger.d { "Check payment with external id $externalId status start" }
        val response: HttpResponse = httpClient.get(
            urlString = apiProvider.paymentInfoUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            parameter("external_id", externalId)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Check payment API request success" }
            val result = response.body<PurchaseDetailsDto>()
            val details = if (paymentId == null) {
                result.details.firstOrNull()
            } else {
                result.details.firstOrNull { it.paymentId == paymentId }
            }
            details?.toCheckPaymentData() ?: throw RozetkaPayPaymentException(
                code = "failure",
                errorMessage = "Payment with id $paymentId not found in purchase details of order $externalId",
            )
        } else {
            Logger.d { "Check payment API request error" }
            response.handlePaymentApiError()
        }
    }

    override suspend fun checkBatchPayment(
        authParameters: ClientAuthParameters,
        externalId: String,
    ): CheckPaymentData {
        Logger.d { "Check batch payment with external id $externalId status start" }
        val response: HttpResponse = httpClient.get(
            urlString = apiProvider.batchPaymentInfoUrl
        ) {
            header("Authorization", "Basic ${authParameters.token}")
            contentType(ContentType.Application.Json)
            parameter("batch_external_id", externalId)
        }
        return if (response.status.isSuccess()) {
            Logger.d { "Check batch payment API request success" }
            val details = response.body<BatchPaymentStatusDto>()
            details.toCheckPaymentData()
        } else {
            Logger.d { "Check payment API request error" }
            response.handlePaymentApiError()
        }
    }

    // common

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