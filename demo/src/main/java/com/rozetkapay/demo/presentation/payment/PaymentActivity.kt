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

class PaymentActivity : ComponentActivity() {
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        // TODO: not supported yet
                    }
                )
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, PaymentActivity::class.java)
    }
}