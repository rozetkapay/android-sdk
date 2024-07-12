package com.rozetkapay.sdk.presentation.tokenization

import androidx.lifecycle.ViewModel
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.domain.models.TokenizationResult
import com.rozetkapay.sdk.domain.models.TokenizedCard
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TokenizationViewModel : ViewModel() {
    private val _resultStateFlow = MutableSharedFlow<TokenizationResult>(replay = 1)
    val resultStateFlow = _resultStateFlow.asSharedFlow()

    fun success() {
        // TODO: tmp solution
        _resultStateFlow.tryEmit(
            TokenizationResult.Complete(
                tokenizedCard = TokenizedCard(
                    token = "demotoken",
                    maskedNumber = "**** **** **** 4242",
                    PaymentSystem.Visa,
                    name = "New card"
                )
            )
        )
    }

    fun error() {
        // TODO: tmp solution
        _resultStateFlow.tryEmit(
            TokenizationResult.Failed()
        )
    }

    fun cancelled() {
        // TODO: tmp solution
        _resultStateFlow.tryEmit(
            TokenizationResult.Cancelled
        )
    }
}
