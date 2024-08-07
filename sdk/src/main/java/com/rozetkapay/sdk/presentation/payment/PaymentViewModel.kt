package com.rozetkapay.sdk.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PaymentViewModel(
    private val client: ClientParameters,
    private val parameters: PaymentParameters,
) : ViewModel() {

    private val _resultStateFlow = MutableSharedFlow<PaymentResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    fun onAction(action: PaymentAction) {
        when (action) {
            PaymentAction.Cancel -> cancelled()
        }
    }

    private fun cancelled() {
        _resultStateFlow.tryEmit(
            PaymentResult.Cancelled
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
            ) as T
        }
    }
}