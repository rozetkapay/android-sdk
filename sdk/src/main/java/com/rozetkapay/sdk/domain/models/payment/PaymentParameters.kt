package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParameters(
    val amountParameters: AmountParameters,
    val allowTokenization: Boolean = true,
) : Parcelable{


    @Parcelize
    data class AmountParameters(
        val amount: Long,
        val currencyCode: String,
    ) : Parcelable
}
