package com.rozetkapay.sdk.domain.errors

class RozetkaPayTokenizationException(
    val id: String,
    val errorMessage: String,
) : Exception("$id: $errorMessage"), RozetkaPayException {

    override fun getReadableMessage(): String = errorMessage
}

