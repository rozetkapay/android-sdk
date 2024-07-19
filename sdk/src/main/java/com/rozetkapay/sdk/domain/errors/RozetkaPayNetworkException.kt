package com.rozetkapay.sdk.domain.errors

class RozetkaPayNetworkException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(message, cause)