package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Immutable
import com.rozetkapay.sdk.domain.models.CardData

@Immutable
internal data class TokenizationUiState(
    val displayState: TokenizationDisplayState = TokenizationDisplayState.Content,
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
    data class Save(val cardData: CardData) : TokenizationAction
    data object Cancel : TokenizationAction
    data object Retry : TokenizationAction
    data class Failed(val reason: Throwable? = null) : TokenizationAction
}