package com.rozetkapay.sdk.presentation.payment.confirmation

import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult

internal data class Confirmation3DsState(
    val url: String,
    val showMainLoading: Boolean = false,
    val showToolbarLoading: Boolean = false,
)

internal sealed class Confirmation3DsAction {
    data object ManuallyClosed : Confirmation3DsAction()
    data object PageLoadingStarted : Confirmation3DsAction()
    data object PageLoadingFinished : Confirmation3DsAction()
    data class UrlChange(val url: String) : Confirmation3DsAction()
    data class UnexpectedUrlError(val url: String) : Confirmation3DsAction()
}

internal sealed interface Confirmation3DsEvent {
    data class Result(
        val result: ConfirmPaymentResult,
    ) : Confirmation3DsEvent
}