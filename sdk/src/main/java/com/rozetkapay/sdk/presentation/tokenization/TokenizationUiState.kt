package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable

@Immutable
data class TokenizationUiState(
    val isInProgress: Boolean = false,
)
