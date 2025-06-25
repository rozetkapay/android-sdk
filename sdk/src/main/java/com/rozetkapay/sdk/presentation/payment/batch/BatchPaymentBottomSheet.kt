package com.rozetkapay.sdk.presentation.payment.batch

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult
import com.rozetkapay.sdk.presentation.components.RozetkaPayBottomSheet
import com.rozetkapay.sdk.presentation.components.rememberRozetkaPayBottomSheetState
import com.rozetkapay.sdk.presentation.forms.card.CardFormViewModel
import com.rozetkapay.sdk.presentation.payment.PaymentScreen
import com.rozetkapay.sdk.presentation.payment.confirmation.Confirmation3DsContract
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BatchPaymentBottomSheet(
    viewModel: BatchPaymentViewModel,
    cardFormViewModel: CardFormViewModel,
    themeConfigurator: RozetkaPayThemeConfigurator,
    onAction: (BatchPaymentBottomSheetAction) -> Unit,
) {
    RozetkaPayTheme(
        themeConfigurator = themeConfigurator
    ) {
        val showSheet = remember { mutableStateOf(true) }
        val modalBottomSheetState = rememberRozetkaPayBottomSheetState()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    is BatchPaymentEvent.Result -> {
                        onAction(BatchPaymentBottomSheetAction.SetResult(event.result))
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showSheet.value = false
                            }
                            onAction(BatchPaymentBottomSheetAction.Finish)
                        }
                    }

                    is BatchPaymentEvent.StartGooglePayPayment -> {
                        onAction(BatchPaymentBottomSheetAction.LaunchGooglePay(event.task))
                    }

                    is BatchPaymentEvent.Start3dsConfirmation -> {
                        onAction(
                            BatchPaymentBottomSheetAction.Launch3ds(
                                Confirmation3DsContract.Parameters(
                                    url = event.url,
                                    themeConfigurator = themeConfigurator,
                                )
                            )
                        )
                    }
                }
            }
        }

        RozetkaPayBottomSheet(
            showSheet = showSheet,
            modalBottomSheetState = modalBottomSheetState,
            onDismiss = {
                onAction(BatchPaymentBottomSheetAction.SetResult(BatchPaymentResult.Cancelled))
                onAction(BatchPaymentBottomSheetAction.Finish)
            }
        ) {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            PaymentScreen(
                state = state,
                cardFormViewModel = cardFormViewModel,
                onAction = { viewModel.onAction(it) },
            )
        }
    }
}

internal sealed class BatchPaymentBottomSheetAction {
    data class SetResult(val result: BatchPaymentResult) : BatchPaymentBottomSheetAction()
    data object Finish : BatchPaymentBottomSheetAction()
    data class LaunchGooglePay(val task: Task<PaymentData>) : BatchPaymentBottomSheetAction()
    data class Launch3ds(val params: Confirmation3DsContract.Parameters) : BatchPaymentBottomSheetAction()
}