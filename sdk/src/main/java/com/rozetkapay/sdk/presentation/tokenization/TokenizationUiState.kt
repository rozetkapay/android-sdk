package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable
import com.rozetkapay.sdk.presentation.components.CardFieldState

@Immutable
internal data class TokenizationUiState(
    val displayState: DisplayState = DisplayState.Content,
    val withCardName: Boolean = false,
    val cardName: String = "",
    val cardNameError: String? = null,
    val withEmail: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val withCardholderName: Boolean = false,
    val cardState: CardFieldState = CardFieldState(),
)

internal sealed class DisplayState {
    data object Loading : DisplayState()
    data object Content : DisplayState()
    data class Error(
        val message: String,
        val reason: Throwable? = null,
    ) : DisplayState()
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