package com.rozetkapay.demo.presentation.tokenization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.presentation.components.Label
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.util.HandleErrorsFlow
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.presentation.tokenization.rememberTokenizationSheet

@Composable
fun TokenizationSeparateScreen(
    onBack: () -> Unit,
) {
    val viewModel = viewModel<CardsViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(errorsFlow = viewModel.errorEventsFlow, snackbarHostState)

    val tokenizationSheet = rememberTokenizationSheet(
        onResultCallback = { result ->
            viewModel.tokenizationFinished(result)
        }
    )
    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Tokenize card",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    tokenizationSheet.show(
                        client = viewModel.clientParameters,
                        parameters = TokenizationParameters(
                            cardNameField = FieldRequirement.Optional,
                            emailField = FieldRequirement.None,
                            cardholderNameField = FieldRequirement.None,
                        ),
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add-icon"
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Add new card"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(bottom = 80.dp)
        ) {
            val cards by viewModel.cards.collectAsState()
            Label(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                text = "You cards:"
            )
            CardsList(
                cards = cards
            )
        }
    }
}

@Composable
@Preview
private fun TokenizationBuiltInScreenPreview() {
    RozetkaPayDemoTheme {
        TokenizationSeparateScreen(
            onBack = {}
        )
    }
}
