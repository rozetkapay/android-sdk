package com.rozetkapay.sdk.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.engine.okhttp.OkHttpEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
internal val jsonConverter = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false

}

internal fun createHttpClient(
    logLevel: LogLevel = LogLevel.HEADERS,
): HttpClient {
    return HttpClient(
        OkHttpEngine(
            config = OkHttpConfig()
        )
    ) {
        install(Logging) {
            level = logLevel
            logger = object : Logger {
                override fun log(message: String) {
                    com.rozetkapay.sdk.util.Logger.d(tag = "RozetkaPaySdk-Network") { message }
                }
            }

        }
        install(ContentNegotiation) {
            json(
                json = jsonConverter
            )
        }
    }
}