package com.rozetkapay.demo.presentation.tokenization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rozetkapay.demo.R
import com.rozetkapay.demo.presentation.components.InDevelopmentBlock
import com.rozetkapay.demo.presentation.components.Label
import com.rozetkapay.demo.presentation.components.SimpleToolbar
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme

@Composable
fun TokenizationBuiltInScreen(
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            SimpleToolbar(
                title = "Tokenization - Build In",
                onBack = onBack
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            val viewModel = viewModel<CardsViewModel>()
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
            Label(
                modifier = Modifier.padding(
                    top = 32.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                text = "Add new card:"
            )
            InDevelopmentBlock(
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp),
                title = "Card tokenization block",
            )
        }
    }
}

@Composable
@Preview
private fun TokenizationBuiltInScreenPreview() {
    RozetkaPayDemoTheme {
        TokenizationBuiltInScreen(
            onBack = {}
        )
    }
}
