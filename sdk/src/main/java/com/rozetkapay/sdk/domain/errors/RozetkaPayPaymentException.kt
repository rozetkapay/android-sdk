package com.rozetkapay.sdk.domain.errors

class RozetkaPayPaymentException(
    val code: String,
    val errorMessage: String,
    val type: String? = null,
) : Exception("code = $code, type = $type, message =  $errorMessage")

