package com.rozetkapay.sdk.presentation.tokenization

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.components.RozetkaPayBottomSheet
import com.rozetkapay.sdk.presentation.components.rememberRozetkaPayBottomSheetState
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.coroutines.launch

internal class TokenizationSheetActivity : BaseRozetkaPayActivity() {

    private val parameters: TokenizationSheetContract.Parameters? by lazy {
        TokenizationSheetContract.Parameters.fromIntent(intent)
    }

    @VisibleForTesting
    internal var viewModelFactory: ViewModelProvider.Factory = TokenizationViewModel.Factory {
        requireNotNull(parameters)
    }

    private val viewModel: TokenizationViewModel by viewModels { viewModelFactory }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RozetkaPayTheme(
                themeConfigurator = parameters?.themeConfigurator ?: RozetkaPayThemeConfigurator()
            ) {
                val showSheet = remember { mutableStateOf(true) }
                val modalBottomSheetState = rememberRozetkaPayBottomSheetState()
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
                    onDismiss = { viewModel.onAction(TokenizationAction.Cancel) }
                ) {
                    val state = viewModel.uiState.collectAsState()
                    TokenizationScreen(
                        state = state.value,
                        onSave = { viewModel.onAction(TokenizationAction.Save) },
                        onCancel = { viewModel.onAction(TokenizationAction.Cancel) },
                        onNameChanged = { viewModel.onAction(TokenizationAction.UpdateName(it)) },
                        onCardFieldStateChanged = { viewModel.onAction(TokenizationAction.UpdateCard(it)) }
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
