package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable
import com.rozetkapay.sdk.presentation.components.CardFieldState

@Immutable
internal data class TokenizationUiState(
    val isInProgress: Boolean = false,
    val withCardName: Boolean = false,
    val cardName: String = "",
    val cardNameError: String? = null,
    val withEmail: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val withCardholderName: Boolean = false,
    val cardState: CardFieldState = CardFieldState(),
)

internal sealed interface TokenizationAction {
    data object Save : TokenizationAction
    data object Cancel : TokenizationAction
    data class UpdateCardName(val name: String) : TokenizationAction
    data class UpdateEmail(val email: String) : TokenizationAction
    data class UpdateCard(val state: CardFieldState) : TokenizationAction
}