package com.rozetkapay.sdk.presentation.payment.googlepay

import org.json.JSONArray
import org.json.JSONObject

internal object GooglePayUtils {

    val baseRequest = JSONObject()
        .put("apiVersion", 2)
        .put("apiVersionMinor", 0)

    val allowedCardNetworks = JSONArray(
        listOf(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA"
        )
    )

    val allowedCardAuthMethods = JSONArray(
        listOf(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS"
        )
    )

    val baseCardPaymentMethod = JSONObject()
        .put("type", "CARD")
        .put(
            "parameters", JSONObject()
                .put("allowedAuthMethods", allowedCardAuthMethods)
                .put("allowedCardNetworks", allowedCardNetworks)
        )
}