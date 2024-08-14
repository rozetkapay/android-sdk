package com.rozetkapay.sdk.presentation.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.components.RozetkaPayBottomSheet
import com.rozetkapay.sdk.presentation.components.rememberRozetkaPayBottomSheetState
import com.rozetkapay.sdk.presentation.payment.confirmation.Confirmation3DsContract
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.coroutines.launch

internal class PaymentSheetActivity : BaseRozetkaPayActivity() {

    private val parameters: PaymentSheetContract.Parameters? by lazy {
        PaymentSheetContract.Parameters.fromIntent(intent)
    }

    @VisibleForTesting
    internal var viewModelFactory: ViewModelProvider.Factory = PaymentViewModel.Factory {
        requireNotNull(parameters)
    }

    private val viewModel: PaymentViewModel by viewModels { viewModelFactory }

    private val googlePayDataLauncher =
        registerForActivityResult(TaskResultContracts.GetPaymentDataResult()) { result ->
            viewModel.onAction(PaymentAction.GooglePayResult(result))
        }
    private val confirm3DsDataLauncher =
        registerForActivityResult(Confirmation3DsContract()) { result ->
            viewModel.onAction(PaymentAction.PaymentConfirmed(result))
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeConfigurator = parameters?.themeConfigurator ?: RozetkaPayThemeConfigurator()
        setContent {
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
                                setActivityResult(event.result)
                                scope.launch {
                                    modalBottomSheetState.hide()
                                }.invokeOnCompletion {
                                    if (!modalBottomSheetState.isVisible) {
                                        showSheet.value = false
                                    }
                                    finish()
                                }
                            }

                            is PaymentEvent.StartGooglePayPayment -> {
                                event.task.addOnCompleteListener(googlePayDataLauncher::launch)
                            }

                            is PaymentEvent.Start3dsConfirmation -> {
                                confirm3DsDataLauncher.launch(
                                    Confirmation3DsContract.Parameters(
                                        paymentId = event.paymentId,
                                        url = event.url,
                                        themeConfigurator = themeConfigurator,
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
                        setActivityResult(PaymentResult.Cancelled)
                        finish()
                    }
                ) {
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    PaymentScreen(
                        state = state,
                        onAction = { viewModel.onAction(it) },
                    )
                }
            }
        }
    }

    private fun setActivityResult(result: PaymentResult) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                PaymentSheetContract.Result(
                    paymentResult = result
                ).toBundle()
            )
        )
    }
}
