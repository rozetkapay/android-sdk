package com.rozetkapay.sdk.presentation.forms.card

import androidx.compose.runtime.Immutable

@Immutable
internal data class CardFormUiState(
    val withCardName: Boolean = false,
    val cardName: String = "",
    val cardNameError: String? = null,
    val withEmail: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val withCardholderName: Boolean = false,
    val cardState: CardFieldState = CardFieldState(),
) {
    val hasErrors: Boolean
        get() = cardState.hasErrors ||
            cardNameError != null ||
            emailError != null
}

internal sealed interface CardFormAction {
    data class UpdateCardName(val name: String) : CardFormAction
    data class UpdateEmail(val email: String) : CardFormAction
    data class UpdateCard(val state: CardFieldState) : CardFormAction
}