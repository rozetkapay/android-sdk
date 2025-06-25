package com.rozetkapay.sdk.domain.errors

class RozetkaPayNetworkException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause), RozetkaPayException {

    override fun getReadableMessage(): String = "Network error: ${message ?: cause?.message}"
}