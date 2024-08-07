package com.rozetkapay.demo.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rozetkapay.demo.config.Credentials
import com.rozetkapay.demo.domain.models.Product
import com.rozetkapay.sdk.domain.models.ClientParameters
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class PaymentViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow(mockedCartItemData)
    val cartItems = _cartItems.asStateFlow()
    val total = _cartItems.map {
        it.sumOf { item -> item.product.price * item.count }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    val clientParameters = ClientParameters(
        widgetKey = Credentials.WIDGET_KEY,
        secretKey = Credentials.SECRET_KEY
    )

    companion object {
        val mockedCartItemData = listOf(
            CartItemData(
                Product(
                    name = "RZTK Buds TWS Black",
                    imageUrl = "https://content1.rozetka.com.ua/goods/images/big/378870632.jpg",
                    price = 62900L
                ),
                count = 1
            ),
            CartItemData(
                Product(
                    name = "Smartphone Case",
                    imageUrl = "https://content1.rozetka.com.ua/goods/images/big/379377051.jpg",
                    price = 22900L
                ),
                count = 2
            ),
            CartItemData(
                Product(
                    name = "RZTK Power Bank",
                    imageUrl = "https://content1.rozetka.com.ua/goods/images/big/426602978.jpg",
                    price = 59900L
                ),
                count = 1
            ),
            CartItemData(
                Product(
                    name = "RZTK Plasma 20W RGB",
                    imageUrl = "https://content1.rozetka.com.ua/goods/images/big/392327478.jpg",
                    price = 119900L
                ),
                count = 1
            ),
        )
    }
}