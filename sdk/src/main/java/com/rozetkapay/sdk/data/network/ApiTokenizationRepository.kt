package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.data.network.converters.TokenizationRequestDto
import com.rozetkapay.sdk.data.network.converters.toTokenizedCard
import com.rozetkapay.sdk.data.network.models.TokenizationErrorDto
import com.rozetkapay.sdk.data.network.models.TokenizationResponseDto
import com.rozetkapay.sdk.domain.errors.RozetkaPayNetworkException
import com.rozetkapay.sdk.domain.errors.RozetkaPayTokenizationException
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.DeviceInfo
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
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
import kotlinx.serialization.json.encodeToJsonElement

internal class ApiTokenizationRepository(
    private val apiProvider: ApiProvider,
    private val httpClient: HttpClient,
    private val requestSigner: RequestSigner,
) : TokenizationRepository {

    override suspend fun tokenizeCard(
        widgetKey: String,
        cardData: CardData,
        email: String?,
        device: DeviceInfo,
    ): TokenizedCard = withContext(Dispatchers.IO) {
        Logger.d { "Start tokenization process for card" }
        val body = TokenizationRequestDto(
            cardData = cardData,
            device = device,
            email = email
        )
        val signature = requestSigner.sign(
            key = widgetKey,
            data = jsonConverter.encodeToJsonElement(body)
        )
        val response = httpClient.post(
            urlString = apiProvider.tokenizationUrl
        ) {
            header("X-Widget-Id", widgetKey)
            header("X-Sign", signature)
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        if (response.status.isSuccess()) {
            Logger.d { "Tokenization success" }
            val responseDto = response.body<TokenizationResponseDto>()
            Logger.d { "Decrypted tokenization response: $responseDto" }
            responseDto.toTokenizedCard()
        } else {
            Logger.d { "Tokenization error" }
            val errorData = response.body<TokenizationErrorDto?>()
            if (errorData != null) {
                throw RozetkaPayTokenizationException(
                    id = errorData.id,
                    errorMessage = errorData.errorMessage,
                )
            } else {
                val bodyString = response.bodyAsText()
                throw RozetkaPayNetworkException(
                    message = "Unknown error, status code: ${response.status.value}, body = $bodyString"
                )
            }
        }
    }
}