package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable
import com.rozetkapay.sdk.presentation.components.CardFieldState

@Immutable
internal data class TokenizationUiState(
    val isInProgress: Boolean = false,
    val withName: Boolean = true,
    val cardName: String = "",
    val cardState: CardFieldState = CardFieldState(),
)

internal sealed interface TokenizationAction {
    data object Save : TokenizationAction
    data object Cancel : TokenizationAction
    data class UpdateName(val name: String) : TokenizationAction
    data class UpdateCard(val state: CardFieldState) : TokenizationAction
}