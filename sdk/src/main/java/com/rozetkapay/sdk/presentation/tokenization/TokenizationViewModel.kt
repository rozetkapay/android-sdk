package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class TokenizationViewModel(
    private val client: ClientParameters,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<TokenizationResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(TokenizationUiState())
    val uiState = _uiState.asStateFlow()

    fun success() {
        // TODO: tmp solution
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(isInProgress = true))
            delay(5000) // emulate network request
            _resultStateFlow.tryEmit(
                TokenizationResult.Complete(
                    tokenizedCard = TokenizedCard(
                        token = "demotoken",
                        name = "New card - ${client.secretKey}",
                        cardInfo = TokenizedCard.CardInfo(
                            maskedNumber = "**** **** **** 4242",
                            paymentSystem = PaymentSystem.Visa,
                            cardType = "CREDIT"
                        )
                    )
                )
            )
        }
    }

    fun error() {
        // TODO: tmp solution
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(isInProgress = true))
            delay(2000) // emulate network request
            _resultStateFlow.tryEmit(
                TokenizationResult.Failed()
            )
        }
    }

    fun cancelled() {
        // TODO: tmp solution
        _resultStateFlow.tryEmit(
            TokenizationResult.Cancelled
        )
    }

    internal class Factory(
        private val parametersSupplier: () -> TokenizationSheetContract.Parameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return TokenizationViewModel(parametersSupplier().client) as T
        }
    }
}
