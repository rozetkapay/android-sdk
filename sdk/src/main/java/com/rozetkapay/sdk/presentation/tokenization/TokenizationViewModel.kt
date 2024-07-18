package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.presentation.components.CardFieldState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class TokenizationViewModel(
    private val client: ClientParameters,
    private val tokenizationParameters: TokenizationParameters,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
    private val parseCardDataUseCase: ParseCardDataUseCase,
    private val tokenizeCardUseCase: TokenizeCardUseCase,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<TokenizationResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(
        TokenizationUiState(
            withName = tokenizationParameters.withName
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: TokenizationAction) {
        when (action) {
            is TokenizationAction.Save -> save()
            is TokenizationAction.Cancel -> cancelled()
            is TokenizationAction.UpdateName -> updateName(action.name)
            is TokenizationAction.UpdateCard -> updateCard(action.state)
        }
    }

    private fun updateCard(state: CardFieldState) {
        val newState = if (uiState.value.cardState.hasErrors) {
            validateCardState(state)
        } else {
            state
        }
        _uiState.tryEmit(
            uiState.value.copy(
                cardState = newState.copy(
                    paymentSystem = provideCardPaymentSystemUseCase.invoke(state.cardNumber)
                )
            )
        )
    }

    private fun updateName(name: String) {
        _uiState.tryEmit(
            uiState.value.copy(cardName = name)
        )
    }

    private fun cancelled() {
        _resultStateFlow.tryEmit(
            TokenizationResult.Cancelled
        )
    }

    private fun validateCardState(state: CardFieldState): CardFieldState {
        val result = parseCardDataUseCase(
            rawCardNumber = state.cardNumber,
            rawCvv = state.cvv,
            rawExpDate = state.expDate
        )
        return if (result is CardParsingResult.Error) {
            return state.copy(
                cardNumberError = result.cardNumberError,
                cvvError = result.cvvError,
                expDateError = result.expDateError
            )
        } else {
            state.copy(
                cardNumberError = null,
                cvvError = null,
                expDateError = null
            )
        }
    }

    private fun save() {
        val cardState = validateCardState(uiState.value.cardState)
        if (cardState.hasErrors) {
            _uiState.tryEmit(
                uiState.value.copy(cardState = cardState)
            )
        } else {
            runTokenization(cardState)
        }
    }

    private fun runTokenization(cardState: CardFieldState) {
        _uiState.tryEmit(
            uiState.value.copy(
                isInProgress = true
            )
        )
        val result = parseCardDataUseCase(
            rawCardNumber = cardState.cardNumber,
            rawCvv = cardState.cvv,
            rawExpDate = cardState.expDate
        )
        if (result is CardParsingResult.Success) {
            tokenizeCardUseCase(
                TokenizeCardUseCase.Parameters(
                    cardData = result.cardData,
                    widgetKey = client.widgetKey,
                    secretKey = client.secretKey
                )
            )
                .catch { error ->
                    _resultStateFlow.tryEmit(
                        TokenizationResult.Failed(error = error)
                    )
                }
                .onEach { tokenizedCard ->
                    _resultStateFlow.tryEmit(
                        TokenizationResult.Complete(tokenizedCard)
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    internal class Factory(
        private val parametersSupplier: () -> TokenizationSheetContract.Parameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            return TokenizationViewModel(
                client = parametersSupplier().client,
                tokenizationParameters = parametersSupplier().parameters,
                provideCardPaymentSystemUseCase = RozetkaPayKoinContext.koin.get(),
                parseCardDataUseCase = RozetkaPayKoinContext.koin.get(),
                tokenizeCardUseCase = RozetkaPayKoinContext.koin.get()
            ) as T
        }
    }
}