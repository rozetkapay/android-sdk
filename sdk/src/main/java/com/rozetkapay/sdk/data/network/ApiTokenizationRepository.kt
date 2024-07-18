package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
import kotlinx.coroutines.delay
import java.util.UUID

internal class ApiTokenizationRepository : TokenizationRepository {

    override suspend fun tokenizeCard(
        widgetKey: String,
        secretKey: String,
        cardData: CardData,
    ): TokenizedCard {
        // TODO: implement real tokenization
        delay(5000)
        if (cardData.number == "0000000000000000") {
            throw Exception("Invalid card number")
        } else {
            return TokenizedCard(
                token = UUID.randomUUID().toString(),
                cardInfo = TokenizedCard.CardInfo(
                    maskedNumber = "**** **** **** 4242",
                    paymentSystem = PaymentSystem.Visa.name,
                    cardType = "CREDIT"
                )
            )
        }
    }
}