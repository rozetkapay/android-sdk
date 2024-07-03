package com.rozetkapay.demo.presentation.tokenization

import androidx.lifecycle.ViewModel
import com.rozetkapay.demo.domain.models.PaymentSystem
import com.rozetkapay.demo.domain.models.TokenizedCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CardsViewModel : ViewModel() {

    private val _cards = MutableStateFlow(mockedCards)
    val cards = _cards.asStateFlow()

    companion object {
        val mockedCards = listOf(
            TokenizedCard(
                token = "token1",
                name = "Mono Black",
                maskedNumber = "**** **** **** 1234",
                paymentSystem = PaymentSystem.Visa
            ),
            TokenizedCard(
                token = "token2",
                name = "Mono White",
                maskedNumber = "**** **** **** 5678",
                paymentSystem = PaymentSystem.MasterCard
            ),
            TokenizedCard(
                token = "token3",
                name = "Oschad Пенсійна",
                maskedNumber = "**** **** **** 9012",
                paymentSystem = PaymentSystem.Other("ПРОСТІР")
            )
        )
    }

}
