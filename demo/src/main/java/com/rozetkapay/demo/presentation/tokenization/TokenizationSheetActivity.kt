package com.rozetkapay.demo.presentation.tokenization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.unit.dp
import com.rozetkapay.demo.presentation.components.Label
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.theme.classicRozetkaPaySdkThemeConfigurator
import com.rozetkapay.demo.presentation.util.HandleErrorsFlow
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.presentation.tokenization.TokenizationSheet

class TokenizationSheetActivity : ComponentActivity() {
    private val viewModel: CardsViewModel by viewModels()
    private lateinit var tokenizationSheet: TokenizationSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        tokenizationSheet = TokenizationSheet(this, viewModel::tokenizationFinished)
        setContent {
            RozetkaPayDemoClassicTheme {
                Screen(
                    viewModel = viewModel,
                    onBack = { finish() },
                    onAddNewCard = {
                        tokenizationSheet.show(
                            client = ClientParameters(
                                key = viewModel.clientSecret
                            ),
                            themeConfigurator = classicRozetkaPaySdkThemeConfigurator
                        )
                    },
                )
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, TokenizationSheetActivity::class.java)
    }
}

@Composable
private fun Screen(
    viewModel: CardsViewModel,
    onBack: () -> Unit,
    onAddNewCard: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    HandleErrorsFlow(errorsFlow = viewModel.errorEventsFlow, snackbarHostState)

    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Tokenization - Separate",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewCard
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
