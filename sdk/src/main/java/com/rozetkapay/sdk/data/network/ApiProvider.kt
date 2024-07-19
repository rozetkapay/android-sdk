package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.RozetkaPaySdkMode

internal interface ApiProvider {
    val tokenizationUrl: String
}

class ApiProviderImpl : ApiProvider {
    private val prodApiUrl = "https://widget.rozetkapay.com"
    private val devApiUrl = "https://widget-epdev.rozetkapay.com"

    override val tokenizationUrl: String
        get() = afterApiUrl("/api/v1/sdk/tokenize")

    private fun afterApiUrl(path: String): String {
        return provideApiUrl() + path
    }

    private fun provideApiUrl(): String {
        return when (RozetkaPaySdk.mode) {
            RozetkaPaySdkMode.Production -> prodApiUrl
            RozetkaPaySdkMode.Development -> devApiUrl
        }
    }
}