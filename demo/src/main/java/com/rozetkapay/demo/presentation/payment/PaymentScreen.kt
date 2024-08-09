package com.rozetkapay.demo.presentation.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.util.HandleErrorsFlow
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.presentation.payment.rememberPaymentSheet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PaymentScreen(
    onBack: () -> Unit,
) {
    val viewModel = viewModel<PaymentViewModel>()
    val cartItems by viewModel.cartItems.collectAsState()
    val total by viewModel.total.collectAsState()

    val paymentSheet = rememberPaymentSheet(
        onResultCallback = { result ->
            viewModel.paymentFinished(result)
        }
    )

    PaymentSheetScreenContent(
        onBack = onBack,
        items = cartItems,
        total = total,
        onCheckout = {
            paymentSheet.show(
                client = viewModel.clientParameters,
                parameters = PaymentParameters(
                    allowTokenization = true,
                    amountParameters = PaymentParameters.AmountParameters(
                        amount = total,
                        currencyCode = "UAH"
                    ),
                    googlePayConfig = viewModel.testGooglePayConfig
                ),
            )
        },
        errorsFlow = viewModel.errorEventsFlow,
    )
}

@Composable
fun PaymentSheetScreenContent(
    items: List<CartItemData>,
    total: Long,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    errorsFlow: Flow<String>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(errorsFlow = errorsFlow, snackbarHostState)

    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Your cart",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = 16.dp,
                            horizontal = 8.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items.forEach {
                        CartItem(item = it)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp
                    ),
                    text = "Shipment: ",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = "Free",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp
                    ),
                    text = "Total: ",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = total.amount(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .height(82.dp)
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    )
                    .fillMaxWidth(),
                onClick = onCheckout
            ) {
                Text(text = "Checkout")
            }
        }
    }
}

@Composable
@Preview
private fun PaymentSheetScreenPreview() {
    RozetkaPayDemoTheme {
        PaymentSheetScreenContent(
            items = PaymentViewModel.mockedCartItemData,
            total = 1000,
            errorsFlow = emptyFlow(),
            onBack = {},
            onCheckout = {}
        )
    }
}
