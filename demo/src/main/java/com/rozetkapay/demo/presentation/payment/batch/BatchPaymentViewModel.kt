package com.rozetkapay.demo.presentation.payment.batch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rozetkapay.demo.presentation.payment.GroupedCartItemsData
import com.rozetkapay.demo.presentation.payment.PaymentCredentials
import com.rozetkapay.demo.presentation.payment.PaymentDataProvider
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BatchPaymentViewModel : ViewModel() {

    private val errorEventsChannel = Channel<String>()
    val errorEventsFlow = errorEventsChannel.receiveAsFlow()

    private val defaultState = BatchPaymentScreenState(
        groups = PaymentDataProvider.groupedCartItems,
        status = BatchPaymentScreenState.Status.Ready
    )
    private val _state = MutableStateFlow(defaultState)
    val state = _state.asStateFlow()

    fun paymentFinished(result: BatchPaymentResult) {
        when (result) {
            is BatchPaymentResult.Complete -> {
                Log.d("Payment", "Batch payment was successful, tokenized card: ${result.tokenizedCard}")
                _state.value = _state.value.copy(
                    status = BatchPaymentScreenState.Status.Completed
                )
            }

            is BatchPaymentResult.Failed -> {
                Log.e("Payment", "Batch payment failed", result.error)
                viewModelScope.launch {
                    if (result.message.isNullOrBlank()) {
                        errorEventsChannel.send("An error occurred during payment process. Please try again.")
                    } else {
                        errorEventsChannel.send("An error with message \"${result.message}\". Please try again.")
                    }
                }
            }

            BatchPaymentResult.Cancelled -> {
                Log.d("Payment", "Batch payment was cancelled manually by user")
            }

            is BatchPaymentResult.Pending -> {
                Log.d("Payment", "Batch payment is pending")
                _state.value = _state.value.copy(
                    status = BatchPaymentScreenState.Status.PaymentPending
                )
            }
        }
    }

    fun reset() {
        _state.tryEmit(defaultState)
    }

    fun generateBatchExternalId(): String {
        return "batch-${System.currentTimeMillis()}"
    }

    fun getOrders(): List<BatchPaymentParameters.Order> {
        return state.value.groups.mapIndexed { index, group ->
            BatchPaymentParameters.Order(
                apiKey = PaymentCredentials.merchantsApiKeys[index % PaymentCredentials.merchantsApiKeys.size],
                externalId = "order-$index-${System.currentTimeMillis()}",
                amount = group.items.sumOf { it.product.price * it.count },
                description = group.items.joinToString(separator = ", ") { it.product.name }
            )
        }
    }
}

data class BatchPaymentScreenState(
    val groups: List<GroupedCartItemsData> = emptyList(),
    val status: Status = Status.Ready,
) {

    val total = groups.sumOf { it.items.sumOf { item -> item.product.price * item.count } }

    enum class Status {
        Ready,
        Completed,
        PaymentPending
    }
}