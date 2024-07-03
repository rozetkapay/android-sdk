package com.rozetkapay.demo.presentation.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.demo.presentation.Route
import com.rozetkapay.demo.presentation.components.ToolbarTitle
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onNavigationEvent: (Route) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ToolbarTitle(title = "Rozetka Pay Demo")
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoCard()
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = "Tokenization - Build In",
                onClick = { onNavigationEvent(Route.TokenizationBuildIn) }
            )
            MenuButton(
                text = "Tokenization - Separate",
                onClick = { onNavigationEvent(Route.TokenizationSeparate) }
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "icon-lamp"
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Welcome to Rozetka Pay Demo!",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "This is a demo application for Rozetka Pay SDK integration. You can try different features of the SDK here.",
                    modifier = Modifier.padding(start = 16.dp),

                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.width(280.dp),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Composable
@Preview
private fun MenuScreenPreview() {
    RozetkaPayDemoTheme {
        MenuScreen(
            onNavigationEvent = {})
    }
}
