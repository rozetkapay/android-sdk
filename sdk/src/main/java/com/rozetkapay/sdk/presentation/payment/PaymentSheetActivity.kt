package com.rozetkapay.sdk.presentation.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.components.RozetkaPayBottomSheet
import com.rozetkapay.sdk.presentation.components.rememberRozetkaPayBottomSheetState
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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            RozetkaPayTheme(
                themeConfigurator = parameters?.themeConfigurator ?: RozetkaPayThemeConfigurator()
            ) {
                val showSheet = remember { mutableStateOf(true) }
                val modalBottomSheetState = rememberRozetkaPayBottomSheetState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.resultStateFlow.collect { result ->
                        setActivityResult(result)
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showSheet.value = false
                            }
                            finish()
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
                    val state by viewModel.uiState.collectAsState()
                    PaymentScreen(
                        state = state,
                        onCardFieldStateChanged = { viewModel.onAction(PaymentAction.UpdateCard(it)) },
                        onTokenizationChanged = { viewModel.onAction(PaymentAction.UpdateTokenization(it)) },
                        onPay = { viewModel.onAction(PaymentAction.Pay) },
                        onCancel = { viewModel.onAction(PaymentAction.Cancel) },
                        onRetry = { viewModel.onAction(PaymentAction.Retry) },
                        onFailed = { viewModel.onAction(PaymentAction.Failed(it)) }
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
