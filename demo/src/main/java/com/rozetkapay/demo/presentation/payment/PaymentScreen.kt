package com.rozetkapay.demo.presentation.payment

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.R
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
    val state by viewModel.state.collectAsStateWithLifecycle()

    val paymentSheet = rememberPaymentSheet(
        onResultCallback = { result ->
            viewModel.paymentFinished(result)
        }
    )

    PaymentSheetScreenContent(
        onBack = onBack,
        state = state,
        onCheckout = {
            paymentSheet.show(
                client = viewModel.clientParameters,
                parameters = PaymentParameters(
                    allowTokenization = true,
                    amountParameters = PaymentParameters.AmountParameters(
                        amount = state.total,
                        currencyCode = "UAH"
                    ),
                    orderId = state.orderId,
                    googlePayConfig = viewModel.exampleGooglePayConfig
                ),
            )
        },
        onReset = viewModel::reset,
        errorsFlow = viewModel.errorEventsFlow,
    )
}

@Composable
fun PaymentSheetScreenContent(
    state: PaymentScreenState,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onReset: () -> Unit,
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
        AnimatedContent(
            targetState = state.isCompleted,
            label = ""
        ) { isCompleted ->
            if (isCompleted) {
                PaymentSuccessScreen(
                    modifier = Modifier.padding(innerPadding),
                    onReset = onReset
                )
            } else {
                PaymentCartScreen(
                    modifier = Modifier.padding(innerPadding),
                    state = state,
                    onCheckout = onCheckout
                )
            }
        }
    }
}

@Composable
private fun PaymentCartScreen(
    modifier: Modifier,
    state: PaymentScreenState,
    onCheckout: () -> Unit,
) {
    Column(
        modifier = modifier
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
                state.items.forEach {
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
                text = state.total.amount(),
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

@Composable
private fun PaymentSuccessScreen(
    modifier: Modifier = Modifier,
    onReset: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    vertical = 24.dp,
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, CenterVertically)
        ) {
            Image(
                modifier = Modifier
                    .size(200.dp),
                painter = painterResource(id = R.drawable.img_success),
                contentDescription = "success-image"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Payment successfully completed!",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        Button(
            modifier = Modifier.widthIn(min = 200.dp),
            onClick = onReset
        ) {
            Text(
                text = "OK",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
@Preview
@Preview(name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PaymentSheetScreenPreview() {
    RozetkaPayDemoTheme {
        PaymentSheetScreenContent(
            state = PaymentScreenState(
                items = PaymentViewModel.mockedCartItemData,
                total = 1000,
                orderId = "demo_order_id",
                isCompleted = false
            ),
            errorsFlow = emptyFlow(),
            onBack = {},
            onReset = {},
            onCheckout = {}
        )
    }
}

@Composable
@Preview
@Preview(name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PaymentSheetScreenCompletedPreview() {
    RozetkaPayDemoTheme {
        PaymentSheetScreenContent(
            state = PaymentScreenState(
                items = PaymentViewModel.mockedCartItemData,
                total = 1000,
                orderId = "demo_order_id",
                isCompleted = true
            ),
            errorsFlow = emptyFlow(),
            onBack = {},
            onReset = {},
            onCheckout = {}
        )
    }
}
