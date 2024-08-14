package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParameters(
    val amountParameters: AmountParameters,
    // unique order id in your system
    val orderId: String,
    // optional, this callback url will be called after payment is finished
    val callbackUrl: String? = null,
    // optional, is card should be tokenized in payment process
    val allowTokenization: Boolean = true,
    // optional, if null Google Pay will be disabled
    val googlePayConfig: GooglePayConfig? = null,
) : Parcelable {

    @Parcelize
    data class AmountParameters(
        val amount: Long,   // amount in coins
        val currencyCode: String, // ISO-4217
    ) : Parcelable
}