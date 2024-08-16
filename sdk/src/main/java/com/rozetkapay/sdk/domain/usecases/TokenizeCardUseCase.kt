package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.TokenizationRepository

internal class TokenizeCardUseCase(
    private val tokenizationRepository: TokenizationRepository,
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
) : ResultUseCase<TokenizeCardUseCase.Parameters, TokenizedCard>() {

    override suspend fun doWork(params: Parameters): TokenizedCard {
        val deviceInfo = getDeviceInfoUseCase()
        val tokenizeCard = tokenizationRepository.tokenizeCard(
            widgetKey = params.widgetKey,
            cardData = params.cardData,
            email = params.email,
            device = deviceInfo
        )
        val paymentSystem = provideCardPaymentSystemUseCase(params.cardData.number)
        return tokenizeCard.copy(
            name = params.cardName,
            cardInfo = tokenizeCard.cardInfo?.copy(
                paymentSystem = paymentSystem?.alias
            )
        )
    }

    data class Parameters(
        val cardData: CardData,
        val cardName: String? = null,
        val email: String? = null,
        val widgetKey: String,
    )
}