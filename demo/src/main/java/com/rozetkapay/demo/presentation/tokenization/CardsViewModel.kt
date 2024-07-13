package com.rozetkapay.demo.presentation.tokenization

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rozetkapay.demo.domain.models.CardToken
import com.rozetkapay.demo.domain.models.PaymentSystem
import com.rozetkapay.demo.domain.models.parsePaymentSystem
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.TokenizationResult
import com.rozetkapay.sdk.domain.models.TokenizedCard
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CardsViewModel : ViewModel() {

    private val _cards = MutableStateFlow(mockedCards)
    val cards = _cards.asStateFlow()

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    val clientParameters = ClientParameters(
        widgetKey = "demo_widget_key",
        secretKey = "demo_secret_key"
    )

    companion object {
        val mockedCards = listOf(
            CardToken(
                token = "token1",
                name = "Mono Black",
                maskedNumber = "**** **** **** 1234",
                paymentSystem = PaymentSystem.Visa
            ),
            CardToken(
                token = "token2",
                name = "Mono White",
                maskedNumber = "**** **** **** 5678",
                paymentSystem = PaymentSystem.MasterCard
            ),
            CardToken(
                token = "token3",
                name = "Oschad Пенсійна",
                maskedNumber = "**** **** **** 9012",
                paymentSystem = PaymentSystem.Other("ПРОСТІР")
            )
        )
    }

    fun tokenizationFinished(result: TokenizationResult) {
        when (result) {
            is TokenizationResult.Complete -> {
                addNewCard(result.tokenizedCard)
            }

            is TokenizationResult.Failed -> {
                viewModelScope.launch {
                    errorEventsChannel.send("An error occurred during tokenization process. Please try again.")
                }
            }

            TokenizationResult.Cancelled -> {
                Log.d("Tokenization", "Tokenization was cancelled")
            }
        }
    }

    private fun addNewCard(tokenizedCard: TokenizedCard) {
        val newCards = _cards.value.toMutableList()
        newCards.add(
            CardToken(
                token = tokenizedCard.token,
                name = tokenizedCard.name ?: "New card",
                maskedNumber = tokenizedCard.cardInfo?.maskedNumber ?: "****",
                paymentSystem = tokenizedCard.cardInfo?.paymentSystem.parsePaymentSystem()
            )
        )
        _cards.value = newCards
    }
}
