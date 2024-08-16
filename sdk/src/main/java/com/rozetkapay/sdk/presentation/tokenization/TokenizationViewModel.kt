package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.errors.RozetkaPayTokenizationException
import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.domain.validators.AlwaysValidValidator
import com.rozetkapay.sdk.domain.validators.EmailValidator
import com.rozetkapay.sdk.domain.validators.OptionalStringValidator
import com.rozetkapay.sdk.domain.validators.RequiredFieldValidator
import com.rozetkapay.sdk.domain.validators.ValidationResult
import com.rozetkapay.sdk.domain.validators.validatorOf
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.presentation.util.isShow
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class TokenizationViewModel(
    private val client: ClientWidgetParameters,
    private val tokenizationParameters: TokenizationParameters,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
    private val parseCardDataUseCase: ParseCardDataUseCase,
    private val tokenizeCardUseCase: TokenizeCardUseCase,
    private val resourcesProvider: ResourcesProvider,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<TokenizationResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(
        TokenizationUiState(
            withCardName = tokenizationParameters.cardNameField.isShow(),
            withEmail = tokenizationParameters.emailField.isShow(),
            withCardholderName = tokenizationParameters.cardholderNameField.isShow(),
            cardState = CardFieldState()
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: TokenizationAction) {
        when (action) {
            is TokenizationAction.Save -> save()
            is TokenizationAction.Cancel -> cancelled()
            is TokenizationAction.UpdateCardName -> updateCardName(action.name)
            is TokenizationAction.UpdateCard -> updateCard(action.state)
            is TokenizationAction.UpdateEmail -> updateEmail(action.email)
            is TokenizationAction.Failed -> failedDueToError(action.reason)
            TokenizationAction.Retry -> retry()
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

    private fun updateCardName(name: String) {
        val validationResult = if (_uiState.value.cardNameError != null) {
            validateCardName(name)
        } else {
            null
        }
        _uiState.tryEmit(
            uiState.value.copy(
                cardName = name,
                cardNameError = (validationResult as? ValidationResult.Error)?.message
            )
        )
    }

    private fun updateEmail(email: String) {
        val validationResult = if (_uiState.value.emailError != null) {
            validateEmail(email)
        } else {
            null
        }
        _uiState.tryEmit(
            uiState.value.copy(
                email = email,
                emailError = (validationResult as? ValidationResult.Error)?.message
            )
        )
    }

    private fun retry() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = TokenizationDisplayState.Content
            )
        )
    }

    private fun cancelled() {
        _resultStateFlow.tryEmit(
            TokenizationResult.Cancelled
        )
    }

    private fun validateCardName(name: String): ValidationResult {
        val validator = when (tokenizationParameters.cardNameField) {
            FieldRequirement.Required -> RequiredFieldValidator(resourcesProvider)
            else -> AlwaysValidValidator
        }
        return validator.validate(name.trim())
    }

    private fun validateEmail(email: String): ValidationResult {
        val validator = when (tokenizationParameters.emailField) {
            FieldRequirement.Required -> validatorOf(EmailValidator(resourcesProvider))
            FieldRequirement.Optional -> OptionalStringValidator(EmailValidator(resourcesProvider))
            FieldRequirement.None -> AlwaysValidValidator
        }
        return validator.validate(email.trim())
    }

    private fun validateCardState(state: CardFieldState): CardFieldState {
        val result = parseCardDataUseCase(
            rawCardNumber = state.cardNumber,
            rawCvv = state.cvv,
            rawExpDate = state.expDate,
            isCardholderNameRequired = tokenizationParameters.cardholderNameField == FieldRequirement.Required,
            rawCardholderName = state.cardholderName
        )
        return if (result is CardParsingResult.Error) {
            return state.copy(
                cardNumberError = result.cardNumberError,
                cvvError = result.cvvError,
                expDateError = result.expDateError,
                cardholderNameError = result.cardholderNameError
            )
        } else {
            state.copy(
                cardNumberError = null,
                cvvError = null,
                expDateError = null,
                cardholderNameError = null
            )
        }
    }

    private fun save() {
        val cardState = validateCardState(uiState.value.cardState)
        val cardNameValue = uiState.value.cardName.trim()
        val nameValidationResult = validateCardName(cardNameValue)
        val emailValue = uiState.value.email.trim()
        val emailValidationResult = validateEmail(emailValue)
        if (cardState.hasErrors
            || nameValidationResult is ValidationResult.Error
            || emailValidationResult is ValidationResult.Error
        ) {
            _uiState.tryEmit(
                uiState.value.copy(
                    cardNameError = (nameValidationResult as? ValidationResult.Error)?.message,
                    emailError = (emailValidationResult as? ValidationResult.Error)?.message,
                    cardState = cardState
                )
            )
        } else {
            runTokenization(
                cardState = cardState,
                cardName = cardNameValue,
                email = emailValue
            )
        }
    }

    private fun runTokenization(
        cardState: CardFieldState,
        cardName: String? = null,
        email: String? = null,
    ) {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = TokenizationDisplayState.Loading
            )
        )
        val result = parseCardDataUseCase(
            rawCardNumber = cardState.cardNumber,
            rawCvv = cardState.cvv,
            rawExpDate = cardState.expDate,
            isCardholderNameRequired = tokenizationParameters.cardholderNameField == FieldRequirement.Required,
            rawCardholderName = cardState.cardholderName
        )
        if (result is CardParsingResult.Success) {
            tokenizeCardUseCase(
                TokenizeCardUseCase.Parameters(
                    cardData = result.cardData,
                    cardName = cardName,
                    email = email,
                    widgetKey = client.widgetKey,
                )
            )
                .catch { error ->
                    Logger.e(throwable = error) { "Tokenization error" }
                    _uiState.tryEmit(
                        uiState.value.copy(
                            displayState = TokenizationDisplayState.Error(
                                message = resourcesProvider.getString(R.string.rozetka_pay_tokenization_error_common)
                            )
                        )
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

    private fun failedDueToError(reason: Throwable? = null) {
        _resultStateFlow.tryEmit(
            TokenizationResult.Failed(
                message = if (reason is RozetkaPayTokenizationException) reason.errorMessage else null,
                error = reason
            )
        )
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
                tokenizeCardUseCase = RozetkaPayKoinContext.koin.get(),
                resourcesProvider = RozetkaPayKoinContext.koin.get()
            ) as T
        }
    }
}