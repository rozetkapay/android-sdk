package com.rozetkapay.demo.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.demo.R
import com.rozetkapay.demo.presentation.components.ToolbarTitle
import com.rozetkapay.demo.presentation.menu.ClassicMenuActivity
import com.rozetkapay.demo.presentation.menu.ComposableMenuActivity
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RozetkaPayDemoTheme {
                AppTypeScreen(
                    onUseComposable = {
                        startActivity(ComposableMenuActivity.startIntent(this))
                    },
                    onUseClassicView = {
                        startActivity(ClassicMenuActivity.startIntent(this))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTypeScreen(
    onUseComposable: () -> Unit,
    onUseClassicView: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RozetkaPayDemoClassicTheme {
                CardButton(
                    icon = painterResource(id = R.drawable.ic_android),
                    title = "Use classic Android View",
                    description = "In this section of the application, you can learn how to work with the RozetkaPay " +
                        "SDK for a classic Android View, using Activities, Fragments, and XML",
                    onClick = onUseClassicView
                )
            }
            CardButton(
                icon = painterResource(id = R.drawable.ic_compose),
                title = "Use Jetpack Compose",
                description = "In this section of the application, you can learn how to work with the RozetkaPay " +
                    "SDK using Jetpack Compose",
                onClick = onUseComposable
            )
        }
    }
}

@Composable
private fun CardButton(
    icon: Painter,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = icon,
                contentDescription = "icon"
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@Preview
private fun AppTypeScreenPreview() {
    RozetkaPayDemoTheme {
        AppTypeScreen(
            onUseComposable = {},
            onUseClassicView = {}
        )
    }
}
