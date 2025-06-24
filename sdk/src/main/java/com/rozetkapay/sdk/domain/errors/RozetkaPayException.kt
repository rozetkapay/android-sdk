package com.rozetkapay.sdk.domain.errors

interface RozetkaPayException {
    fun getReadableMessage(): String?
}