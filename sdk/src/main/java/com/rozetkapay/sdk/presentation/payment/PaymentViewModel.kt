package com.rozetkapay.sdk.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.errors.RozetkaPayTokenizationException
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.Currency
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.util.MoneyFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal class PaymentViewModel(
    private val client: ClientParameters,
    private val parameters: PaymentParameters,
    private val resourcesProvider: ResourcesProvider,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
    private val parseCardDataUseCase: ParseCardDataUseCase,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<PaymentResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(
        PaymentUiState(
            allowTokenization = parameters.allowTokenization,
            amountWithCurrency = MoneyFormatter.formatCoinsToMoney(
                coins = parameters.amountParameters.amount,
                currency = Currency.getSymbol(parameters.amountParameters.currencyCode)
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: PaymentAction) {
        when (action) {
            PaymentAction.Cancel -> cancelled()
            PaymentAction.Retry -> retry()
            is PaymentAction.UpdateCard -> updateCard(action.state)
            is PaymentAction.Failed -> failedDueToError(action.reason)
            PaymentAction.Pay -> pay()
            is PaymentAction.UpdateTokenization -> updateTokenization(action.value)
        }
    }

    private fun pay() {
        val cardState = validateCardState(uiState.value.cardState)
        if (cardState.hasErrors) {
            _uiState.tryEmit(
                uiState.value.copy(
                    cardState = cardState
                )
            )
        } else {
            runPaymentWithCard(
                cardState = cardState,
            )
        }
    }

    private fun runPaymentWithCard(cardState: CardFieldState) {
        // payment with card data not supported yet
        // it will be implemented in the future releases
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = PaymentDisplayState.Error(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_unsupported)
                )
            )
        )
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

    private fun updateTokenization(value: Boolean) {
        _uiState.tryEmit(
            uiState.value.copy(
                tokenize = value
            )
        )
    }

    private fun validateCardState(state: CardFieldState): CardFieldState {
        val result = parseCardDataUseCase(
            rawCardNumber = state.cardNumber,
            rawCvv = state.cvv,
            rawExpDate = state.expDate,
            isCardholderNameRequired = false,
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

    private fun retry() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = PaymentDisplayState.Content
            )
        )
    }

    private fun cancelled() {
        _resultStateFlow.tryEmit(
            PaymentResult.Cancelled
        )
    }

    private fun failedDueToError(reason: Throwable? = null) {
        _resultStateFlow.tryEmit(
            PaymentResult.Failed(
                message = if (reason is RozetkaPayTokenizationException) reason.errorMessage else null,
                error = reason
            )
        )
    }

    internal class Factory(
        private val parametersSupplier: () -> PaymentSheetContract.Parameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            return PaymentViewModel(
                client = parametersSupplier().client,
                parameters = parametersSupplier().parameters,
                resourcesProvider = RozetkaPayKoinContext.koin.get(),
                provideCardPaymentSystemUseCase = RozetkaPayKoinContext.koin.get(),
                parseCardDataUseCase = RozetkaPayKoinContext.koin.get()
            ) as T
        }
    }
}