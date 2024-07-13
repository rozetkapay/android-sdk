package com.rozetkapay.sdk.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenizedCard(
    val token: String,
    val name: String? = null,
    val cardInfo: CardInfo? = null,
) : Parcelable {

    @Parcelize
    data class CardInfo(
        val maskedNumber: String? = null,
        val paymentSystem: String? = null,
        val bank: String? = null,
        val isoA3Code: String? = null,
        val cardType: String? = null,
    ) : Parcelable
}
