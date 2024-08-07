package com.rozetkapay.sdk.presentation.payment

internal sealed interface PaymentAction {
    data object Cancel : PaymentAction
}