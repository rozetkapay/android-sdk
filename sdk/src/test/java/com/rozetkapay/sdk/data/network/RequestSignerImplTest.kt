package com.rozetkapay.sdk.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class RequestSignerImplTest {

    @Test
    fun `check request signature`() {
        val requestSigner = RequestSignerImpl()
        val data = TestData(
            b = "2",
            a = "1",
            c = "3"
        )
        assert(
            requestSigner.sign(
                key = "secret",
                data = Json.encodeToJsonElement(data)
            ) == "77de38e4b50e618a0ebb95db61e2f42697391659d82c064a5f81b9f48d85ccd5"
        )
    }

    @Serializable
    private data class TestData(
        @SerialName("b")
        val b: String,
        @SerialName("a")
        val a: String,
        @SerialName("c")
        val c: String,
    )
}