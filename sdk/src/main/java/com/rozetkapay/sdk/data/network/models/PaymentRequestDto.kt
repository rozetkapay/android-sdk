package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object PaymentApiConstants {
    const val MODE_DIRECT = "direct"
    const val MODE_HOSTED = "hosted"

    const val PAYMENT_METHOD_TYPE_GOOGLE_PAY = "google_pay"
    const val PAYMENT_METHOD_TYPE_CARD_TOKEN = "cc_token"
}

@Serializable
internal data class PaymentRequestDto(
    @SerialName("amount")
    val amount: Double,
    @SerialName("currency")
    val currency: String,
    @SerialName("external_id")
    val externalId: String,
    @SerialName("callback_url")
    val callbackUrl: String? = null,
    @SerialName("mode")
    val mode: String = PaymentApiConstants.MODE_DIRECT,
    @SerialName("customer")
    val customer: CustomerDto,
)

@Serializable
internal data class CustomerDto(
    @SerialName("payment_method")
    val paymentMethod: PaymentMethodDto,
)

@Serializable
internal data class PaymentMethodDto(
    @SerialName("type")
    val type: String,
    @SerialName("google_pay")
    val googlePay: GooglePayDto? = null,
    @SerialName("cc_token")
    val cardToken: CardTokenDto? = null,
) {

    companion object {

        fun googlePay(googlePay: GooglePayDto): PaymentMethodDto {
            return PaymentMethodDto(
                type = PaymentApiConstants.PAYMENT_METHOD_TYPE_GOOGLE_PAY,
                googlePay = googlePay
            )
        }

        fun cardToken(cardToken: CardTokenDto): PaymentMethodDto {
            return PaymentMethodDto(
                type = PaymentApiConstants.PAYMENT_METHOD_TYPE_CARD_TOKEN,
                cardToken = cardToken
            )
        }
    }
}

@Serializable
internal data class GooglePayDto(
    @SerialName("token")
    val token: String,
    @SerialName("use_3ds_flow")
    val use3dsFlow: Boolean = true,
)

@Serializable
internal data class CardTokenDto(
    @SerialName("token")
    val token: String,
    @SerialName("use_3ds_flow")
    val use3dsFlow: Boolean = true,
)