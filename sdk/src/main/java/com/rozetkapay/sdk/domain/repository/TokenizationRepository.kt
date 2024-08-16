package com.rozetkapay.sdk.domain.repository

import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.DeviceInfo
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard

internal interface TokenizationRepository {
    suspend fun tokenizeCard(
        widgetKey: String,
        cardData: CardData,
        email: String?,
        device: DeviceInfo,
    ): TokenizedCard
}