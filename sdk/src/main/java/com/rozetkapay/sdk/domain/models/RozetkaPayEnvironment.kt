package com.rozetkapay.sdk.domain.models

internal data class RozetkaPayEnvironment(
    val tokenizationApiProviderUrl: String,
    val paymentsApiProviderUrl: String,
    val paymentsConfirmation3DsCallbackUrl: String,
)