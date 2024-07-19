package com.rozetkapay.sdk.data.network.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenizationResponseDto(
    @SerialName("token")
    val token: String,
    @SerialName("expires_at")
    val expiresAt: LocalDateTime,
    @SerialName("card_mask")
    val cardMask: String,
    @SerialName("issuer")
    val issuer: Issuer,
) {
    @Serializable
    data class Issuer(
        @SerialName("bank")
        val bank: String?,
        @SerialName("iso_a3_code")
        val isoA3Code: String?,
        @SerialName("card_type")
        val cardType: String?,
    )
}