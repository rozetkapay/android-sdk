package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParameters(
    val amountParameters: AmountParameters,
    // unique order id in your system
    val orderId: String,
    // optional, this callback url will be called after payment is finished
    val callbackUrl: String? = null,
    // payment type configuration
    val paymentType: PaymentTypeConfiguration = RegularPayment(),
) : Parcelable {

    @Parcelize
    data class AmountParameters(
        val amount: Long,   // amount in coins
        val currencyCode: String, // ISO-4217
    ) : Parcelable
}

sealed interface PaymentTypeConfiguration : Parcelable

/**
 * This payment configuration is default and used for regular payment
 * with card data ot Google Pay
 **/
@Parcelize
data class RegularPayment(
    // describe additional card fields configuration
    val cardFieldsParameters: CardFieldsParameters = CardFieldsParameters(),
    // optional, is card should be tokenized in payment process,
    // if true - token will be returned in payment response
    val allowTokenization: Boolean = true,
    // optional, if null Google Pay will be disabled
    val googlePayConfig: GooglePayConfig? = null,
) : PaymentTypeConfiguration

/**
 * This payment configuration is used for payment with tokenized card.
 * Tokenized card is a parameter and payment can be done only with this token,
 * user will be not able to enter card data or select any other card
 */
@Parcelize
data class SingleTokenPayment(
    // card token to use for payment
    val token: String,
) : PaymentTypeConfiguration
