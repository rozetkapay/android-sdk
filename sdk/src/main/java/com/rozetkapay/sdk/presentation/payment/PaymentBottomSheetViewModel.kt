package com.rozetkapay.sdk.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.presentation.payment.googlepay.GooglePayInteractor
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal abstract class PaymentBottomSheetViewModel(
    protected val googlePayInteractor: GooglePayInteractor?,
) : ViewModel() {

    protected abstract val _uiState: MutableStateFlow<PaymentUiState>
    abstract val uiState: StateFlow<PaymentUiState>

    protected suspend fun verifyGooglePayReadiness() {
        if (googlePayInteractor?.fetchCanUseGooglePay() == true) {
            _uiState.tryEmit(
                uiState.value.copy(
                    allowGooglePay = true,
                    googlePayAllowedPaymentMethods = googlePayInteractor.getAllowedPaymentMethods().toString()
                )
            )
        } else {
            Logger.d { "Google Pay is not available for this device" }
            _uiState.tryEmit(
                uiState.value.copy(
                    allowGooglePay = false,
                    googlePayAllowedPaymentMethods = ""
                )
            )
        }
    }

    protected fun checkGooglePayParameters(googlePayConfig: GooglePayConfig?) {
        if (googlePayConfig is GooglePayConfig.Test) {
            Log.w(
                Logger.DEFAULT_TAG,
                """
                ⚠️ WARNING: GOOGLE PAY IS CONFIGURED IN TEST MODE! ⚠️
                ⚠️ THIS IS A DEVELOPMENT CONFIGURATION AND SHOULD NOT BE USED IN PRODUCTION. ⚠️
                DETAILS:
                - Gateway: ${googlePayConfig.gateway}
                - Merchant ID: ${googlePayConfig.merchantId}
                
                Please ensure this configuration is switched to Production mode before releasing the app.
                """.trimIndent()
            )
        }
    }

    abstract fun onAction(action: PaymentAction)
}