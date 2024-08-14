package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.init.RozetkaPaySdkMode

internal interface ApiProvider {
    val tokenizationUrl: String
    val createPaymentUrl: String
}

internal class ApiProviderImpl : ApiProvider {
    private val tokenizationApiProvider = ApiUrlProvider(
        prodUrl = "https://widget.rozetkapay.com",
        devUrl = "https://widget-epdev.rozetkapay.com",
    )
    private val paymentsApiProvider = ApiUrlProvider(
        prodUrl = "https://api.rozetkapay.com",
        // TODO: local test server, should be replaced with public dev server before release
        devUrl = "http://10.10.11.185:3000",
        // public dev server
        // devUrl = "https://api-epdev.rozetkapay.com",
    )

    override val tokenizationUrl: String
        get() = tokenizationApiProvider.afterApiUrl("/api/v1/sdk/tokenize")

    override val createPaymentUrl: String
        get() = paymentsApiProvider.afterApiUrl("/api/payments/v1/new")
}

private class ApiUrlProvider(
    private val prodUrl: String,
    private val devUrl: String,
) {

    fun afterApiUrl(path: String): String {
        return provideUrl() + path
    }

    fun provideUrl(): String {
        return when (RozetkaPaySdk.mode) {
            RozetkaPaySdkMode.Production -> prodUrl
            RozetkaPaySdkMode.Development -> devUrl
        }
    }
}