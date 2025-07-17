package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.errors.RozetkaPayException
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class TokenizationViewModel(
    private val client: ClientWidgetParameters,
    private val tokenizeCardUseCase: TokenizeCardUseCase,
    private val resourcesProvider: ResourcesProvider,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<TokenizationResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(TokenizationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: TokenizationAction) {
        when (action) {
            is TokenizationAction.Save -> save(action.cardData)
            is TokenizationAction.Cancel -> cancelled()
            is TokenizationAction.Failed -> failedDueToError(action.reason)
            TokenizationAction.Retry -> retry()
        }
    }

    private fun retry() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = TokenizationDisplayState.Content
            )
        )
    }

    private fun cancelled() {
        viewModelScope.launch {
            _resultStateFlow.emit(
                TokenizationResult.Cancelled
            )
            resetForm()
        }
    }

    private fun loading() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = TokenizationDisplayState.Loading
            )
        )
    }

    private fun save(cardData: CardData) {
        loading()
        tokenizeCardUseCase(
            TokenizeCardUseCase.Parameters(
                cardData = cardData,
                widgetKey = client.widgetKey,
            )
        ).catch { error ->
            Logger.e(throwable = error) { "Tokenization error" }
            _uiState.emit(
                uiState.value.copy(
                    displayState = TokenizationDisplayState.Error(
                        message = resourcesProvider.getString(R.string.rozetka_pay_tokenization_error_common)
                    )
                )
            )
        }.onEach { tokenizedCard ->
            _resultStateFlow.emit(
                TokenizationResult.Complete(tokenizedCard)
            )
            resetForm()
        }.launchIn(viewModelScope)
    }

    private fun failedDueToError(reason: Throwable? = null) {
        viewModelScope.launch {
            _resultStateFlow.emit(
                TokenizationResult.Failed(
                    message = if (reason is RozetkaPayException) reason.getReadableMessage() else null,
                    error = reason
                )
            )
            resetForm()
        }
    }

    private fun resetForm() {
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    displayState = TokenizationDisplayState.Content
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
            return TokenizationViewModel(
                client = parametersSupplier().client,
                tokenizeCardUseCase = RozetkaPayKoinContext.koin.get(),
                resourcesProvider = RozetkaPayKoinContext.koin.get()
            ) as T
        }
    }
}