package com.rozetkapay.demo.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rozetkapay.demo.config.Credentials
import com.rozetkapay.demo.domain.models.Product
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    private val defaultState = PaymentScreenState(
        items = mockedCartItemData,
        total = mockedCartItemData.sumOf { it.product.price * it.count },
        status = PaymentScreenState.Status.Ready
    )
    private val _state = MutableStateFlow(defaultState)
    val state = _state.asStateFlow()

    val clientParameters = ClientAuthParameters(
        token = Credentials.DEV_AUTH_TOKEN
    )

    val testGooglePayConfig = GooglePayConfig.Test(
        merchantId = Credentials.GOOGLE_PAY_MERCHANT_ID,
        merchantName = Credentials.GOOGLE_PAY_MERCHANT_NAME
    )

    // this is Google Pay configuration for testing purposes
    // proposed to use in tutorial https://developers.google.com/pay/api/android/guides/tutorial
    val exampleGooglePayConfig = GooglePayConfig.Test(
        gateway = "example",
        merchantId = "exampleGatewayMerchantId",
        merchantName = Credentials.GOOGLE_PAY_MERCHANT_NAME
    )

    fun paymentFinished(result: PaymentResult) {
        when (result) {
            is PaymentResult.Complete -> {
                Log.d("Payment", "Payment ${result.paymentId} was successful")
                _state.value = _state.value.copy(
                    status = PaymentScreenState.Status.Completed
                )
            }

            is PaymentResult.Failed -> {
                Log.e("Payment", "Payment ${result.paymentId} failed", result.error)
                viewModelScope.launch {
                    if (result.message.isNullOrBlank()) {
                        errorEventsChannel.send("An error occurred during payment process. Please try again.")
                    } else {
                        errorEventsChannel.send("An error with message \"${result.message}\". Please try again.")
                    }
                }
            }

            PaymentResult.Cancelled -> {
                Log.d("Payment", "Payment was cancelled manually by user")
            }

            is PaymentResult.Pending -> {
                Log.d("Payment", "Payment ${result.paymentId} is pending")
                _state.value = _state.value.copy(
                    status = PaymentScreenState.Status.PaymentPending
                )
            }
        }
    }

    fun reset() {
        _state.tryEmit(defaultState)
    }

    fun generateOrderId(): String {
        return "order-${System.currentTimeMillis()}"
    }

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

data class PaymentScreenState(
    val items: List<CartItemData> = emptyList(),
    val total: Long = 0L,
    val status: Status = Status.Ready,
) {
    enum class Status {
        Ready,
        Completed,
        PaymentPending
    }
}