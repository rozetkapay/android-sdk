package com.rozetkapay.sdk.presentation.forms.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.required
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.presentation.util.isShow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CardFormViewModel(
    val parameters: CardFieldsParameters,
    private val parseCardDataUseCase: ParseCardDataUseCase,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CardFormUiState(
            withCardName = parameters.cardNameField.isShow(),
            withEmail = parameters.emailField.isShow(),
            withCardholderName = parameters.cardholderNameField.isShow(),
            cardState = CardFieldState()
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: CardFormAction) {
        when (action) {
            is CardFormAction.UpdateCardName -> updateCardName(action.name)
            is CardFormAction.UpdateEmail -> updateEmail(action.email)
            is CardFormAction.UpdateCard -> updateCard(action.state)
        }
    }

    fun parseCardData(): CardParsingResult {
        val (uiState, result) = validateState(uiState.value)
        _uiState.tryEmit(uiState)
        return result
    }

    private fun updateCard(state: CardFieldState) {
        val complementedState = state.copy(
            paymentSystem = provideCardPaymentSystemUseCase(state.cardNumber)
        )
        val newUiState = if (uiState.value.hasErrors) {
            validateState(uiState.value.copy(cardState = complementedState)).first
        } else {
            uiState.value.copy(cardState = complementedState)
        }
        _uiState.tryEmit(newUiState)
    }

    private fun updateEmail(email: String) {
        val newUiState = if (_uiState.value.emailError != null) {
            validateState(uiState.value.copy(email = email)).first
        } else {
            uiState.value.copy(email = email)
        }
        _uiState.tryEmit(newUiState)
    }

    private fun updateCardName(name: String) {
        val newUiState = if (_uiState.value.cardNameError != null) {
            validateState(uiState.value.copy(cardName = name)).first
        } else {
            uiState.value.copy(cardName = name)
        }
        _uiState.tryEmit(newUiState)
    }

    private fun validateState(state: CardFormUiState): Pair<CardFormUiState, CardParsingResult> {
        val result = parseCardDataUseCase(
            params = ParseCardDataUseCase.Parameters(
                rawCardNumber = state.cardState.cardNumber,
                rawCvv = state.cardState.cvv,
                rawExpDate = state.cardState.expDate,
                rawCardholderName = state.cardState.cardholderName,
                rawCardholderEmail = state.email,
                rawCardName = state.cardName,
                isCardholderNameRequired = parameters.cardholderNameField.required(),
                isCardholderEmailRequired = parameters.emailField.required(),
                isCardNameRequired = parameters.cardNameField.required(),
            )
        )
        val newState = if (result is CardParsingResult.Error) {
            state.copy(
                cardState = state.cardState.copy(
                    cardNumberError = result.cardNumberError,
                    cvvError = result.cvvError,
                    expDateError = result.expDateError,
                    cardholderNameError = result.cardholderNameError,
                ),
                emailError = result.cardholderEmailError,
                cardNameError = result.cardNameError
            )
        } else {
            state.copy(
                cardState = state.cardState.copy(
                    cardNumberError = null,
                    cvvError = null,
                    expDateError = null,
                    cardholderNameError = null,
                ),
                emailError = null,
                cardNameError = null
            )
        }
        return Pair(newState, result)
    }

    internal class Factory(
        private val parametersSupplier: () -> CardFieldsParameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            return CardFormViewModel(
                parameters = parametersSupplier(),
                parseCardDataUseCase = RozetkaPayKoinContext.koin.get(),
                provideCardPaymentSystemUseCase = RozetkaPayKoinContext.koin.get(),
            ) as T
        }
    }
}