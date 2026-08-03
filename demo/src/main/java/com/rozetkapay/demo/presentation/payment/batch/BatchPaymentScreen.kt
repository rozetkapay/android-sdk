package com.rozetkapay.demo.presentation.payment.batch

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
import com.rozetkapay.demo.presentation.payment.CheckoutOption
import com.rozetkapay.demo.presentation.payment.GroupedCartItems
import com.rozetkapay.demo.presentation.payment.PaymentCredentials
import com.rozetkapay.demo.presentation.payment.PaymentDataProvider
import com.rozetkapay.demo.presentation.payment.PaymentMessageScreen
import com.rozetkapay.demo.presentation.payment.amount
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.util.HandleErrorsFlow
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.domain.models.payment.GooglePayPayment
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.domain.models.payment.SingleTokenPayment
import com.rozetkapay.sdk.presentation.payment.batch.rememberBatchPaymentSheet
import com.rozetkapay.sdk.presentation.payment.googlepay.GooglePayButton
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun BatchPaymentScreen(
    onBack: () -> Unit,
) {
    val viewModel = viewModel<BatchPaymentViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val batchPaymentSheet = rememberBatchPaymentSheet(
        onResultCallback = { result ->
            viewModel.paymentFinished(result)
        }
    )

    BatchPaymentScreenContent(
        onBack = onBack,
        state = state,
        onCheckout = { option ->
            batchPaymentSheet.show(
                clientAuthParameters = PaymentCredentials.clientParametersDev,
                parameters = BatchPaymentParameters(
                    orders = viewModel.getOrders(),
                    externalId = viewModel.generateBatchExternalId(),
                    currencyCode = "UAH",
                    callbackUrl = "https://example.com/callback",
                    paymentType = when (option) {
                        CheckoutOption.TokenizedCard -> SingleTokenPayment(
                            token = PaymentCredentials.testCardToken,
                        )

                        CheckoutOption.Card -> RegularPayment(
                            allowTokenization = true,
                            cardFieldsParameters = CardFieldsParameters(),
                            googlePayConfig = PaymentCredentials.testGooglePayConfig,
                        )

                        CheckoutOption.GooglePay -> GooglePayPayment(
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
fun BatchPaymentScreenContent(
    state: BatchPaymentScreenState,
    onBack: () -> Unit,
    onCheckout: (option: CheckoutOption) -> Unit,
    onReset: () -> Unit,
    errorsFlow: Flow<String>,
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
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
                BatchPaymentScreenState.Status.Ready -> {
                    BatchPaymentCartScreen(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onCheckout = onCheckout,
                        themeConfigurator = themeConfigurator,
                    )
                }

                BatchPaymentScreenState.Status.Completed -> {
                    PaymentMessageScreen(
                        modifier = Modifier.padding(innerPadding),
                        message = "Payment successfully completed!",
                        onReset = onReset
                    )
                }

                BatchPaymentScreenState.Status.PaymentPending -> {
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
private fun BatchPaymentCartScreen(
    modifier: Modifier,
    state: BatchPaymentScreenState,
    onCheckout: (option: CheckoutOption) -> Unit,
    themeConfigurator: RozetkaPayThemeConfigurator,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                    horizontal = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.groups.forEach {
                GroupedCartItems(
                    data = it
                )
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
        GooglePayButton(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 32.dp,
                    end = 16.dp,
                )
                .fillMaxWidth(),
            googlePayConfig = PaymentCredentials.testGooglePayConfig,
            themeConfigurator = themeConfigurator,
            onClick = { onCheckout(CheckoutOption.GooglePay) },
        )
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
                            if (useToken.value) CheckoutOption.TokenizedCard else CheckoutOption.Card
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
private fun BatchPaymentScreenContentPreview() {
    RozetkaPayDemoTheme {
        BatchPaymentScreenContent(
            state = BatchPaymentScreenState(
                groups = PaymentDataProvider.groupedCartItems,
                status = BatchPaymentScreenState.Status.Ready
            ),
            errorsFlow = emptyFlow(),
            onBack = {},
            onReset = {},
            onCheckout = {}
        )
    }
}