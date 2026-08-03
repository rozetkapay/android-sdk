package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import kotlinx.parcelize.Parcelize

sealed interface PaymentTypeConfiguration : Parcelable

/**
 * This payment configuration is default and used for regular payment
 * with card data ot Google Pay
 *
 * @property cardFieldsParameters describes additional card fields configuration
 * @property allowTokenization if true, card will be tokenized in payment process
 * @property googlePayConfig optional configuration for Google Pay, if null Google Pay will be disabled
 **/
@Parcelize
data class RegularPayment(
    val cardFieldsParameters: CardFieldsParameters = CardFieldsParameters(),
    val allowTokenization: Boolean = true,
    val googlePayConfig: GooglePayConfig? = null,
) : PaymentTypeConfiguration

/**
 * This payment configuration is used for payment with tokenized card.
 * Tokenized card is a parameter and payment can be done only with this token,
 * user will be not able to enter card data or select any other card
 *
 * @property token card token to use for payment
 */
@Parcelize
data class SingleTokenPayment(
    val token: String,
) : PaymentTypeConfiguration

/**
 * This payment configuration launches the payment flow directly into Google Pay,
 * skipping the card-form / payment-method-selection UI entirely. Intended to be used
 * together with the standalone `GooglePayButton` composable
 * (see `com.rozetkapay.sdk.presentation.payment.googlepay.GooglePayButton`), so that
 * pressing a Google Pay button placed in the host app's own UI goes straight to the
 * Google Pay sheet instead of showing the full payment sheet.
 *
 * @property googlePayConfig configuration required to complete the Google Pay flow
 */
@Parcelize
data class GooglePayPayment(
    val googlePayConfig: GooglePayConfig,
) : PaymentTypeConfiguration
