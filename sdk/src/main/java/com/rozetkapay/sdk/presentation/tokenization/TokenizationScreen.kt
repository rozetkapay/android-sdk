package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.presentation.components.ErrorScreen
import com.rozetkapay.sdk.presentation.components.LegalIconsBlock
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.PrimaryButton
import com.rozetkapay.sdk.presentation.components.SheetCloseHeader
import com.rozetkapay.sdk.presentation.components.Title
import com.rozetkapay.sdk.presentation.components.inSheetPaddings
import com.rozetkapay.sdk.presentation.forms.card.CardFormScreen
import com.rozetkapay.sdk.presentation.forms.card.CardFormViewModel
import com.rozetkapay.sdk.presentation.forms.card.MOCK_CARD_FORM_VIEWMODEL
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun TokenizationScreen(
    state: TokenizationUiState,
    cardFormViewModel: CardFormViewModel,
    onSave: (CardData) -> Unit,
    onCancel: () -> Unit,
    onFailed: (reason: Throwable?) -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .verticalScroll(rememberScrollState())
            .inSheetPaddings()
    ) {
        when (state.displayState) {
            TokenizationDisplayState.Content -> {
                SheetCloseHeader(
                    onClose = onCancel
                )
                TokenizationContent(
                    cardFormViewModel = cardFormViewModel,
                    onSave = onSave,
                )
            }

            is TokenizationDisplayState.Error -> {
                ErrorScreen(
                    message = state.displayState.message,
                    onRetry = onRetry,
                    onCancel = {
                        onFailed(state.displayState.reason)
                    }
                )
            }

            TokenizationDisplayState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

@Composable
private fun TokenizationContent(
    cardFormViewModel: CardFormViewModel,
    onSave: (CardData) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Title(
            title = stringResource(id = R.string.rozetka_pay_tokenization_title)
        )
        Spacer(modifier = Modifier.height(28.dp))
        CardFormScreen(
            viewModel = cardFormViewModel
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_tokenization_save_button),
            onClick = {
                val result = cardFormViewModel.parseCardData()
                if (result is CardParsingResult.Success) {
                    onSave(result.cardData)
                }
            }
        )
        LegalIconsBlock(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentPreview() {
    RozetkaPayTheme {
        TokenizationScreen(
            state = TokenizationUiState(
                displayState = TokenizationDisplayState.Content,
            ),
            cardFormViewModel = MOCK_CARD_FORM_VIEWMODEL,
            onSave = {},
            onCancel = {},
            onFailed = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentProgressPreview() {
    RozetkaPayTheme {
        TokenizationScreen(
            state = TokenizationUiState(
                displayState = TokenizationDisplayState.Loading,
            ),
            cardFormViewModel = MOCK_CARD_FORM_VIEWMODEL,
            onSave = {},
            onCancel = {},
            onFailed = {},
            onRetry = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentFailedPreview() {
    RozetkaPayTheme {
        TokenizationScreen(
            state = TokenizationUiState(
                displayState = TokenizationDisplayState.Error(
                    message = stringResource(id = R.string.rozetka_pay_tokenization_error_common)
                ),
            ),
            cardFormViewModel = MOCK_CARD_FORM_VIEWMODEL,
            onSave = {},
            onCancel = {},
            onFailed = {},
            onRetry = {}
        )
    }
}
