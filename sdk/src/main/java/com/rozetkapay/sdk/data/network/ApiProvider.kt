package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.domain.models.RozetkaPayEnvironment

internal class ApiProvider(
    private val environment: RozetkaPayEnvironment,
) {
    val tokenizationUrl: String
        get() = environment.tokenizationApiProviderUrl.afterApiUrl("/api/v1/sdk/tokenize")

    val createPaymentUrl: String
        get() = environment.paymentsApiProviderUrl.afterApiUrl("/api/payments/v1/new")

    val paymentInfoUrl: String
        get() = environment.paymentsApiProviderUrl.afterApiUrl("/api/payments/v1/info")

    private fun String.afterApiUrl(path: String): String {
        return this + path
    }
}