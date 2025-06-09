package com.rozetkapay.demo.presentation.payment.regular

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
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.domain.models.payment.SingleTokenPayment
import com.rozetkapay.sdk.presentation.payment.regular.PaymentSheet

class PaymentActivity : ComponentActivity() {
    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        paymentSheet = PaymentSheet(
            activity = this,
            callback = viewModel::paymentFinished
        )

        setContent {
            RozetkaPayDemoClassicTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                PaymentScreenContent(
                    state = state,
                    errorsFlow = viewModel.errorEventsFlow,
                    onBack = { finish() },
                    onReset = viewModel::reset,
                    onCheckout = { useToken ->
                        paymentSheet.show(
                            clientAuthParameters = PaymentCredentials.clientParametersProd,
                            parameters = PaymentParameters(
                                amountParameters = PaymentParameters.AmountParameters(
                                    amount = state.total,
                                    currencyCode = "UAH"
                                ),
                                externalId = viewModel.generateOrderId(),
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
        fun startIntent(context: Context) = Intent(context, PaymentActivity::class.java)
    }
}