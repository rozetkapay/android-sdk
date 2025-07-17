package com.rozetkapay.demo.presentation.tokenization

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationFormParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.usecases.TokenizationStringResourcesProvider
import com.rozetkapay.sdk.presentation.tokenization.form.TokenizationForm

@Composable
fun TokenizationFormScreen(
    onBack: () -> Unit,
) {
    val viewModel = viewModel<CardsViewModel>()
    val context = LocalContext.current
    TokenizationFormScreenContent(
        clientWidgetParameters = viewModel.clientWidgetParameters,
        onBack = onBack,
        onResult = {
            viewModel.tokenizationFinished(it)
            Log.d("Tokenization", "Tokenization result: $it")
            if (it is TokenizationResult.Complete) {
                Toast.makeText(context, "Token: ${it.tokenizedCard.token}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
private fun TokenizationFormScreenContent(
    clientWidgetParameters: ClientWidgetParameters,
    onBack: () -> Unit,
    onResult: (TokenizationResult) -> Unit,
) {
    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Build-in tokenization form",
                onBack = onBack
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            var isSaveCardChecked by remember { mutableStateOf(false) }
            TokenizationForm(
                client = clientWidgetParameters,
                parameters = TokenizationFormParameters(
                    cardFieldsParameters = CardFieldsParameters(
                        cardNameField = FieldRequirement.Optional,
                        emailField = FieldRequirement.Optional,
                        cardholderNameField = FieldRequirement.Optional,
                    ),
                    showCardFormTitle = false,
                    showLegalBlock = false,
                    stringResourcesProvider = object : TokenizationStringResourcesProvider {
                        override val saveButtonTitle: String
                            get() = "Do tokenization"
                    }
                ),
                cardFormFooterContent = {
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSaveCardChecked,
                            onCheckedChange = {
                                isSaveCardChecked = it
                            }
                        )
                        Text(
                            text = "Save card to wallet",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                onResult = onResult
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(
    backgroundColor = 0xFF000000,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun TokenizationFormScreenContentPreview() {
    RozetkaPayDemoTheme {
        TokenizationFormScreenContent(
            clientWidgetParameters = ClientWidgetParameters(
                widgetKey = "demo_widget_key",
            ),
            onBack = {},
            onResult = { }
        )
    }
}