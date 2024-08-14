package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenizationErrorDto(
    @SerialName("id")
    val id: String,
    @SerialName("error_message")
    val errorMessage: String,
)