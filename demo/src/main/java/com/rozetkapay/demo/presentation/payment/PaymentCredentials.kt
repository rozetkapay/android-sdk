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

    val testCardToken = Credentials.PROD_TEST_CARD_TOKEN_1
    val errorCardToken = Credentials.ERROR_CARD_TOKEN_1

    // this is Google Pay configuration for testing purposes
    // proposed to use in tutorial https://developers.google.com/pay/api/android/guides/tutorial
    val exampleGooglePayConfig = GooglePayConfig.Test(
        gateway = "example",
        merchantId = "exampleGatewayMerchantId",
        merchantName = Credentials.GOOGLE_PAY_MERCHANT_NAME
    )

    val merchantsApiKeys = listOf(
        "0c1b93ff-da7b-4eec-be5c-c5edd0d4fd17",
        "ef316c93-8dd5-4d86-8366-8174880ec52c"
    )
}