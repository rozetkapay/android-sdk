package com.rozetkapay.demo.presentation.payment

import com.rozetkapay.demo.config.Credentials
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig

@Suppress("unused", "MayBeConstant")
object PaymentCredentials {

    val clientParametersDev = ClientAuthParameters(
        token = Credentials.DEV_AUTH_TOKEN,
        widgetKey = Credentials.DEV_WIDGET_KEY
    )

    val clientParametersProd = ClientAuthParameters(
        token = Credentials.PROD_AUTH_TOKEN,
        widgetKey = Credentials.PROD_WIDGET_KEY
    )

    val testGooglePayConfig = GooglePayConfig.Test(
        merchantId = Credentials.GOOGLE_PAY_MERCHANT_ID,
        merchantName = Credentials.GOOGLE_PAY_MERCHANT_NAME
    )

    val testCardToken = Credentials.DEV_TEST_CARD_TOKEN_1
    val errorCardToken = Credentials.ERROR_CARD_TOKEN_1

    // this is Google Pay configuration for testing purposes
    // proposed to use in tutorial https://developers.google.com/pay/api/android/guides/tutorial
    val exampleGooglePayConfig = GooglePayConfig.Test(
        gateway = "example",
        merchantId = "exampleGatewayMerchantId",
        merchantName = Credentials.GOOGLE_PAY_MERCHANT_NAME
    )

    val merchantsApiKeys = listOf(
        "ce9ac06e-fce8-40ea-b018-3075957fe080",
        "0e329f16-c617-46f1-aac2-63205153eccc",
    )
}