package com.rozetkapay.sdk.presentation.payment.regular

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
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
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
internal fun RegularPaymentBottomSheet(
    viewModel: PaymentViewModel,
    cardFormViewModel: CardFormViewModel,
    themeConfigurator: RozetkaPayThemeConfigurator,
    onAction: (RegularPaymentBottomSheetAction) -> Unit,
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
                    is PaymentEvent.Result -> {
                        onAction(RegularPaymentBottomSheetAction.SetResult(event.result))
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showSheet.value = false
                            }
                            onAction(RegularPaymentBottomSheetAction.Finish)
                        }
                    }

                    is PaymentEvent.StartGooglePayPayment -> {
                        onAction(RegularPaymentBottomSheetAction.LaunchGooglePay(event.task))
                    }

                    is PaymentEvent.Start3dsConfirmation -> {
                        onAction(
                            RegularPaymentBottomSheetAction.Launch3ds(
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
                onAction(RegularPaymentBottomSheetAction.SetResult(PaymentResult.Cancelled))
                onAction(RegularPaymentBottomSheetAction.Finish)
            },
            resourceId = "bottomSheetRegularPayment",
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

internal sealed class RegularPaymentBottomSheetAction {
    data class SetResult(val result: PaymentResult) : RegularPaymentBottomSheetAction()
    data object Finish : RegularPaymentBottomSheetAction()
    data class LaunchGooglePay(val task: Task<PaymentData>) : RegularPaymentBottomSheetAction()
    data class Launch3ds(val params: Confirmation3DsContract.Parameters) : RegularPaymentBottomSheetAction()
}