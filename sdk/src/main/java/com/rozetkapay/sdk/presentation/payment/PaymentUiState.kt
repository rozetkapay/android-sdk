package com.rozetkapay.sdk.presentation.payment

import androidx.compose.runtime.Immutable
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.domain.models.payment.PaymentResult

@Immutable
internal data class PaymentUiState(
    val displayState: PaymentDisplayState = PaymentDisplayState.Content,
    val amountWithCurrency: String = "",
    val allowGooglePay: Boolean = false,
    val googlePayAllowedPaymentMethods: String = "",
)

internal sealed class PaymentDisplayState {
    data object Loading : PaymentDisplayState()
    data object Content : PaymentDisplayState()
    data class Error(
        val message: String,
        val reason: Throwable? = null,
    ) : PaymentDisplayState()
}

internal sealed interface PaymentAction {
    data object Cancel : PaymentAction
    data object Retry : PaymentAction
    data class PayWithCard(val cardData: CardData) : PaymentAction
    data object PayWithGooglePay : PaymentAction
    data class Failed(val reason: Throwable? = null) : PaymentAction
    data class GooglePayResult(val result: ApiTaskResult<PaymentData>) : PaymentAction
    data class PaymentConfirmed(val result: ConfirmPaymentResult) : PaymentAction
}

internal sealed interface PaymentEvent {
    data class Result(
        val result: PaymentResult,
    ) : PaymentEvent

    data class StartGooglePayPayment(
        val task: Task<PaymentData>,
    ) : PaymentEvent

    data class Start3dsConfirmation(
        val paymentId: String,
        val url: String,
    ) : PaymentEvent
}