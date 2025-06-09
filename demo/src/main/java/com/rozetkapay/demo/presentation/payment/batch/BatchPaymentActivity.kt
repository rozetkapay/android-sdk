package com.rozetkapay.demo.presentation.payment.batch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rozetkapay.demo.presentation.payment.PaymentCredentials
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.theme.classicRozetkaPaySdkThemeConfigurator
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.domain.models.payment.SingleTokenPayment
import com.rozetkapay.sdk.presentation.payment.batch.BatchPaymentSheet

class BatchPaymentActivity : ComponentActivity() {
    private val viewModel: BatchPaymentViewModel by viewModels()
    private lateinit var batchPaymentSheet: BatchPaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        batchPaymentSheet = BatchPaymentSheet(
            activity = this,
            callback = viewModel::paymentFinished
        )

        setContent {
            RozetkaPayDemoClassicTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                BatchPaymentScreenContent(
                    state = state,
                    errorsFlow = viewModel.errorEventsFlow,
                    onBack = { finish() },
                    onReset = viewModel::reset,
                    onCheckout = { useToken ->
                        batchPaymentSheet.show(
                            clientAuthParameters = PaymentCredentials.clientParametersProd,
                            parameters = BatchPaymentParameters(
                                orders = viewModel.getOrders(),
                                externalId = viewModel.generateBatchExternalId(),
                                currencyCode = "UAH",
                                callbackUrl = "https://example.com/callback",
                                paymentType = if (useToken) {
                                    SingleTokenPayment(
                                        token = PaymentCredentials.testCardToken,
                                    )
                                } else {
                                    RegularPayment(
                                        allowTokenization = false,
                                        cardFieldsParameters = CardFieldsParameters(),
                                        googlePayConfig = PaymentCredentials.testGooglePayConfig,
                                    )
                                }
                            ),
                            themeConfigurator = classicRozetkaPaySdkThemeConfigurator
                        )
                    }
                )
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, BatchPaymentActivity::class.java)
    }
}