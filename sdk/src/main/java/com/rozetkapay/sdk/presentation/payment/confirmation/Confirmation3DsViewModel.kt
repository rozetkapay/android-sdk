package com.rozetkapay.sdk.presentation.payment.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.domain.RozetkaPayConfig
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.URI

internal class Confirmation3DsViewModel(
    private val url: String,
    private val paymentId: String,
) : ViewModel() {

    private var isFirstPageLoaded: Boolean = false

    private val _eventsChannel = Channel<Confirmation3DsEvent>()
    val eventsFlow = _eventsChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        Confirmation3DsState(
            url = url,
            showMainLoading = !isFirstPageLoaded,
            showToolbarLoading = isFirstPageLoaded
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Confirmation3DsAction) {
        when (action) {

            Confirmation3DsAction.ManuallyClosed -> {
                viewModelScope.launch {
                    _eventsChannel.send(
                        Confirmation3DsEvent.Result(ConfirmPaymentResult.Cancelled)
                    )
                }
            }

            Confirmation3DsAction.PageLoadingStarted -> {
                _uiState.value = _uiState.value.copy(
                    showToolbarLoading = isFirstPageLoaded,
                    showMainLoading = !isFirstPageLoaded
                )
            }

            Confirmation3DsAction.PageLoadingFinished -> {
                isFirstPageLoaded = true
                _uiState.value = _uiState.value.copy(
                    showToolbarLoading = false,
                    showMainLoading = false
                )
            }

            is Confirmation3DsAction.UrlChange -> {
                Logger.d { "URL changed: ${action.url}" }
                handleNewUrl(action.url)
            }

            is Confirmation3DsAction.UnexpectedUrlError -> {
                viewModelScope.launch {
                    _eventsChannel.send(
                        Confirmation3DsEvent.Result(
                            ConfirmPaymentResult.Error(
                                paymentId = paymentId,
                                message = "Unexpected URL: ${action.url} on 3DS, only network URLs allowed",
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleNewUrl(newUrl: String) {
        val uri = URI(newUrl)
        when {
            uri.path.equals(
                URI(RozetkaPayConfig.PAYMENT_3DS_CALLBACK_URL_ERROR).path,
                ignoreCase = true
            ) -> viewModelScope.launch {
                _eventsChannel.send(
                    Confirmation3DsEvent.Result(
                        ConfirmPaymentResult.Error(
                            paymentId = paymentId,
                        )
                    )
                )
            }

            uri.path.equals(
                URI(RozetkaPayConfig.PAYMENT_3DS_CALLBACK_URL_SUCCESS).path,
                ignoreCase = true
            ) -> viewModelScope.launch {
                _eventsChannel.send(
                    Confirmation3DsEvent.Result(
                        ConfirmPaymentResult.Success(
                            paymentId = paymentId,
                        )
                    )
                )
            }

            else -> {
                _uiState.tryEmit(
                    _uiState.value.copy(
                        url = newUrl,
                    )
                )
            }
        }
    }

    internal class Factory(
        private val parametersSupplier: () -> Confirmation3DsContract.Parameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            val parameters = parametersSupplier()
            return Confirmation3DsViewModel(
                url = parameters.url,
                paymentId = parameters.url,
            ) as T
        }
    }
}