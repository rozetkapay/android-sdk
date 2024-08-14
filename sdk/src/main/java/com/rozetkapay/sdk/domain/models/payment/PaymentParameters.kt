package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParameters(
    val amountParameters: AmountParameters,
    val orderId: String,
    val allowTokenization: Boolean = true,
    val googlePayConfig: GooglePayConfig? = null,
) : Parcelable {

    @Parcelize
    data class AmountParameters(
        val amount: Long,
        val currencyCode: String, // ISO-4217
    ) : Parcelable
}