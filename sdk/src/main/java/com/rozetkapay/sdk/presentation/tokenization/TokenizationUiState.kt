package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable
import com.rozetkapay.sdk.presentation.components.CardFieldState

@Immutable
internal data class TokenizationUiState(
    val displayState: TokenizationDisplayState = TokenizationDisplayState.Content,
    val withCardName: Boolean = false,
    val cardName: String = "",
    val cardNameError: String? = null,
    val withEmail: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val withCardholderName: Boolean = false,
    val cardState: CardFieldState = CardFieldState(),
)

internal sealed class TokenizationDisplayState {
    data object Loading : TokenizationDisplayState()
    data object Content : TokenizationDisplayState()
    data class Error(
        val message: String,
        val reason: Throwable? = null,
    ) : TokenizationDisplayState()
}

internal sealed interface TokenizationAction {
    data object Save : TokenizationAction
    data object Cancel : TokenizationAction
    data object Retry : TokenizationAction
    data class Failed(val reason: Throwable? = null) : TokenizationAction
    data class UpdateCardName(val name: String) : TokenizationAction
    data class UpdateEmail(val email: String) : TokenizationAction
    data class UpdateCard(val state: CardFieldState) : TokenizationAction
}