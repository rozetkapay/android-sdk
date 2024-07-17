package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.data.android.AndroidResourcesProvider
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.validators.CardExpDateValidator
import com.rozetkapay.sdk.domain.validators.CardNumberValidator
import com.rozetkapay.sdk.domain.validators.CvvValidator
import com.rozetkapay.sdk.presentation.components.CardFieldState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class TokenizationViewModel(
    private val client: ClientParameters,
    private val tokenizationParameters: TokenizationParameters,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
    private val parseCardDataUseCase: ParseCardDataUseCase,
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
            runTokenization()
        }
    }

    private fun runTokenization() {
        // TODO: stub
        viewModelScope.launch {
            _uiState.tryEmit(
                uiState.value.copy(
                    isInProgress = true
                )
            )
            delay(5000) // emulate network request
            _resultStateFlow.tryEmit(
                TokenizationResult.Complete(
                    tokenizedCard = TokenizedCard(
                        token = "demotoken",
                        name = uiState.value.cardName,
                        cardInfo = TokenizedCard.CardInfo(
                            maskedNumber = "**** **** **** 4242",
                            paymentSystem = PaymentSystem.Visa.name,
                            cardType = "CREDIT"
                        )
                    )
                )
            )
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

            val application = checkNotNull(extras[APPLICATION_KEY])
            val resourcesProvider = AndroidResourcesProvider(application)
            return TokenizationViewModel(
                client = parametersSupplier().client,
                tokenizationParameters = parametersSupplier().parameters,
                provideCardPaymentSystemUseCase = ProvideCardPaymentSystemUseCase(),
                parseCardDataUseCase = ParseCardDataUseCase(
                    cardNumberValidator = CardNumberValidator(resourcesProvider),
                    cvvValidator = CvvValidator(resourcesProvider),
                    expDateValidator = CardExpDateValidator(resourcesProvider),
                    resourcesProvider = resourcesProvider
                )
            ) as T
        }
    }
}