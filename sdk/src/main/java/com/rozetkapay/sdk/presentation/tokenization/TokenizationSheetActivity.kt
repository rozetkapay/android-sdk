package com.rozetkapay.sdk.presentation.tokenization

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.domain.models.TokenizationResult
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.components.RozetkaPayBottomSheet
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.coroutines.launch

internal class TokenizationSheetActivity : BaseRozetkaPayActivity() {

    private val parameters: TokenizationSheetContract.Parameters? by lazy {
        TokenizationSheetContract.Parameters.fromIntent(intent)
    }

    private val viewModel: TokenizationViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback {
            viewModel.cancelled()
        }
        setContent {
            RozetkaPayTheme(
                themeConfigurator = parameters?.themeConfigurator ?: RozetkaPayThemeConfigurator()
            ) {
                val showSheet = remember { mutableStateOf(true) }
                val modalBottomSheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    viewModel.resultStateFlow.collect { result ->
                        setActivityResult(result)
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            if (!modalBottomSheetState.isVisible) {
                                showSheet.value = false
                            }
                            finish()
                        }
                    }
                }

                RozetkaPayBottomSheet(
                    showSheet = showSheet,
                    modalBottomSheetState = modalBottomSheetState,
                    onDismiss = { viewModel.cancelled() }
                ) {
                    TokenizationContent(
                        onSuccess = {
                            viewModel.success()
                        },
                        onFailure = {
                            viewModel.error()
                        }
                    )
                }
            }
        }
    }

    private fun setActivityResult(result: TokenizationResult) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                TokenizationSheetContract.Result(
                    tokenizationResult = result
                ).toBundle()
            )
        )
    }
}

@Composable
private fun TokenizationContent(
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.run {
            spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colorScheme.onSurface,
            text = "Tokenization bottom sheet",
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            color = MaterialTheme.colorScheme.onSurface,
            text = "In development",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onSuccess) {
            Text(text = "Success button")
        }
        Button(onClick = onFailure) {
            Text(text = "Error button")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentPreview() {
    RozetkaPayTheme {
        TokenizationContent(
            onSuccess = {},
            onFailure = {},
        )
    }
}
