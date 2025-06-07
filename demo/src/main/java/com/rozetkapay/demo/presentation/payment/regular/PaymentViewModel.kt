package com.rozetkapay.demo.presentation.payment.regular

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rozetkapay.demo.presentation.payment.CartItemData
import com.rozetkapay.demo.presentation.payment.PaymentDataProvider
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
        items = PaymentDataProvider.cartItems,
        total = PaymentDataProvider.cartItems.sumOf { it.product.price * it.count },
        status = PaymentScreenState.Status.Ready
    )
    private val _state = MutableStateFlow(defaultState)
    val state = _state.asStateFlow()

    fun paymentFinished(result: PaymentResult) {
        when (result) {
            is PaymentResult.Complete -> {
                Log.d("Payment", "Payment ${result.paymentId} was successful, tokenized card: ${result.tokenizedCard}")
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