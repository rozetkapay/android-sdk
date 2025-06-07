package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This class contains all parameters required for payment.
 *
 * @property amountParameters object containing amount and currency code for payment
 * @property externalId unique external id of payment in your system (for example can be orderId)
 * @property callbackUrl optional callback URL that will be called after payment is finished
 * @property paymentType configuration for the payment type, default is [RegularPayment]
 */
@Parcelize
data class PaymentParameters(
    val amountParameters: AmountParameters,
    val externalId: String,
    val callbackUrl: String? = null,
    val paymentType: PaymentTypeConfiguration = RegularPayment(),
) : Parcelable {

    /**
     * Represents amount and currency code for payment.
     *
     * @property amount amount in coins (1 coin = 0.01 UAH)
     * @property currencyCode ISO-4217 currency code (for example "UAH" for Ukrainian Hryvnia)
     */
    @Parcelize
    data class AmountParameters(
        val amount: Long,
        val currencyCode: String,
    ) : Parcelable
}