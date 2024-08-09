package com.rozetkapay.demo.presentation.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.theme.classicRozetkaPaySdkThemeConfigurator
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.presentation.payment.PaymentSheet

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
                val items by viewModel.cartItems.collectAsState()
                val total by viewModel.total.collectAsState()
                PaymentSheetScreenContent(
                    items = items,
                    total = total,
                    errorsFlow = viewModel.errorEventsFlow,
                    onBack = { finish() },
                    onCheckout = {
                        paymentSheet.show(
                            client = viewModel.clientParameters,
                            parameters = PaymentParameters(
                                allowTokenization = false,
                                amountParameters = PaymentParameters.AmountParameters(
                                    amount = total,
                                    currencyCode = "EUR"
                                ),
                                googlePayConfig = viewModel.testGooglePayConfig,
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