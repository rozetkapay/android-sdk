package com.rozetkapay.sdk.data.network

import com.rozetkapay.sdk.util.Logger
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.SortedMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal interface RequestSigner {
    fun sign(key: String, data: JsonElement): String
}

internal class RequestSignerImpl : RequestSigner {
    override fun sign(
        key: String,
        data: JsonElement,
    ): String {
        Logger.d { "Start signing request, data = $data" }
        if (data !is JsonObject) {
            throw IllegalArgumentException("Only JsonObject is supported.")
        }
        val sortedItems: SortedMap<String, JsonElement> = data.toMap().toSortedMap()
        val body = sortedItems.entries.joinToString("") { it.value.asStringValue() }

        Logger.d { "Joined body = $body" }

        val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256").apply {
            init(secretKeySpec)
        }
        val hmacBytes = mac.doFinal(body.toByteArray(Charsets.UTF_8))
        return hmacBytes.joinToString("") { "%02x".format(it) }
    }

    private fun JsonElement.asStringValue(): String {
        return when (this) {
            is JsonArray -> this.map { it.asStringValue() }.sorted().joinToString(",")
            is JsonPrimitive -> this.content
            JsonNull -> ""
            else -> throw IllegalArgumentException("${this.javaClass} is not currently supported.")
        }
    }
}

