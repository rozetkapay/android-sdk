package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.TokenizationRepository

internal class TokenizeCardUseCase(
    private val tokenizationRepository: TokenizationRepository,
) : ResultUseCase<TokenizeCardUseCase.Parameters, TokenizedCard>() {

    override suspend fun doWork(params: Parameters): TokenizedCard {
        return tokenizationRepository.tokenizeCard(
            widgetKey = params.widgetKey,
            secretKey = params.secretKey,
            cardData = params.cardData,
        )
    }

    data class Parameters(
        val cardData: CardData,
        val widgetKey: String,
        val secretKey: String,
    )
}