package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.TokenizationRequestDto
import com.rozetkapay.sdk.data.network.models.TokenizationResponseDto
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.DeviceInfo
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard

internal fun TokenizationRequestDto(
    cardData: CardData,
    device: DeviceInfo,
    email: String?,
): TokenizationRequestDto = TokenizationRequestDto(
    cardNumber = cardData.number,
    cardExpYear = cardData.expDate.year,
    cardExpMonth = cardData.expDate.month,
    cardCvv = cardData.cvv,
    cardholderName = cardData.cardholderName,
    platform = device.platform,
    sdkVersion = device.sdkVersion,
    osVersion = device.osVersion,
    osBuildVersion = device.osBuildVersion,
    osBuildNumber = device.osBuildNumber,
    deviceId = device.deviceId,
    customerEmail = email
)

internal fun TokenizationResponseDto.toTokenizedCard(): TokenizedCard {
    return TokenizedCard(
        token = token,
        cardInfo = TokenizedCard.CardInfo(
            maskedNumber = this.cardMask,
            paymentSystem = null,
            bank = this.issuer.bank,
            isoA3Code = this.issuer.isoA3Code,
            cardType = this.issuer.cardType,
        )
    )
}