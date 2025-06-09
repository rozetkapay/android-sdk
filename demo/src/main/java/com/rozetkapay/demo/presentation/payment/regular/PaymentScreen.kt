package com.rozetkapay.demo.presentation.payment.regular

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.payment.CartItem
import com.rozetkapay.demo.presentation.payment.PaymentCredentials
import com.rozetkapay.demo.presentation.payment.PaymentDataProvider
import com.rozetkapay.demo.presentation.payment.PaymentMessageScreen
import com.rozetkapay.demo.presentation.payment.amount
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.util.HandleErrorsFlow
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.domain.models.payment.SingleTokenPayment
import com.rozetkapay.sdk.presentation.payment.regular.rememberPaymentSheet
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

    PaymentScreenContent(
        onBack = onBack,
        state = state,
        onCheckout = { useToken ->
            paymentSheet.show(
                clientAuthParameters = PaymentCredentials.clientParametersProd,
                parameters = PaymentParameters(
                    amountParameters = PaymentParameters.AmountParameters(
                        amount = state.total,
                        currencyCode = "UAH"
                    ),
                    externalId = viewModel.generateOrderId(),
                    callbackUrl = "https://example.com/callback",
                    paymentType = if (useToken) {
                        SingleTokenPayment(
                            token = PaymentCredentials.testCardToken,
                        )
                    } else {
                        RegularPayment(
                            allowTokenization = true,
                            cardFieldsParameters = CardFieldsParameters(),
                            googlePayConfig = PaymentCredentials.testGooglePayConfig,
                        )
                    }
                ),
            )
        },
        onReset = viewModel::reset,
        errorsFlow = viewModel.errorEventsFlow,
    )
}

@Composable
fun PaymentScreenContent(
    state: PaymentScreenState,
    onBack: () -> Unit,
    onCheckout: (useToken: Boolean) -> Unit,
    onReset: () -> Unit,
    errorsFlow: Flow<String>,
) {
    HandleErrorsFlow(errorsFlow = errorsFlow)
    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Your cart",
                onBack = onBack
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.status,
            label = ""
        ) { status ->
            when (status) {
                PaymentScreenState.Status.Ready -> {
                    PaymentCartScreen(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onCheckout = onCheckout
                    )
                }

                PaymentScreenState.Status.Completed -> {
                    PaymentMessageScreen(
                        modifier = Modifier.padding(innerPadding),
                        message = "Payment successfully completed!",
                        onReset = onReset
                    )
                }

                PaymentScreenState.Status.PaymentPending -> {
                    PaymentMessageScreen(
                        modifier = Modifier.padding(innerPadding),
                        message = "Your payment is currently pending. Please check back later for an update. " +
                            "We'll process your order as soon as the payment is successful. ",
                        onReset = onReset
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentCartScreen(
    modifier: Modifier,
    state: PaymentScreenState,
    onCheckout: (useToken: Boolean) -> Unit,
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 16.dp
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val useToken = remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = CenterVertically,
                ) {
                    Checkbox(
                        checked = useToken.value,
                        onCheckedChange = { useToken.value = it },
                    )
                    Text(
                        text = "Use tokenized card",
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    onClick = {
                        onCheckout(
                            useToken.value
                        )
                    }
                ) {
                    Text(text = "Checkout")
                }
            }
        }
    }
}

@Composable
@Preview
@Preview(name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun PaymentScreenContentPreview() {
    RozetkaPayDemoTheme {
        PaymentScreenContent(
            state = PaymentScreenState(
                items = PaymentDataProvider.cartItems,
                total = 1000,
                status = PaymentScreenState.Status.Ready
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
private fun PaymentScreenContentMessagePreview() {
    RozetkaPayDemoTheme {
        PaymentScreenContent(
            state = PaymentScreenState(
                items = PaymentDataProvider.cartItems,
                total = 1000,
                status = PaymentScreenState.Status.PaymentPending
            ),
            errorsFlow = emptyFlow(),
            onBack = {},
            onReset = {},
            onCheckout = {}
        )
    }
}
