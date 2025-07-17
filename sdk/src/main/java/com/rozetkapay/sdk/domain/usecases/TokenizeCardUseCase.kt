package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.CardExpDate
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
import java.util.Locale

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
            device = deviceInfo
        )
        val paymentSystem = provideCardPaymentSystemUseCase(params.cardData.number)
        return tokenizeCard.copy(
            name = params.cardData.cardName,
            cardInfo = tokenizeCard.cardInfo?.copy(
                expiresAt = params.cardData.expDate.formatted(),
                paymentSystem = paymentSystem?.alias
            )
        )
    }

    private fun CardExpDate.formatted(): String {
        return String.format(
            Locale.US,
            "%02d/%02d",
            this.month,
            this.year
        )
    }

    data class Parameters(
        val cardData: CardData,
        val widgetKey: String,
    )
}