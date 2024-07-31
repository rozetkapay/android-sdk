package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.components.CardField
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.presentation.components.ErrorScreen
import com.rozetkapay.sdk.presentation.components.FormTextField
import com.rozetkapay.sdk.presentation.components.LegalBlock
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.PrimaryButton
import com.rozetkapay.sdk.presentation.components.SheetCloseHeader
import com.rozetkapay.sdk.presentation.components.Subtitle
import com.rozetkapay.sdk.presentation.components.Title
import com.rozetkapay.sdk.presentation.components.inSheetPaddings
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun TokenizationScreen(
    state: TokenizationUiState,
    onCardNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onCardFieldStateChanged: (CardFieldState) -> Unit,
    onSave: () -> Unit,
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
            DisplayState.Content -> {
                SheetCloseHeader(
                    onClose = onCancel
                )
                TokenizationContent(
                    state = state,
                    onSave = onSave,
                    onCardNameChanged = onCardNameChanged,
                    onCardFieldStateChanged = onCardFieldStateChanged,
                    onEmailChanged = onEmailChanged
                )
            }

            is DisplayState.Error -> {
                ErrorScreen(
                    message = state.displayState.message,
                    onRetry = onRetry,
                    onCancel = {
                        onFailed(state.displayState.reason)
                    }
                )
            }

            DisplayState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

@Composable
private fun TokenizationContent(
    state: TokenizationUiState,
    onCardNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onCardFieldStateChanged: (CardFieldState) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Title(
            title = stringResource(id = R.string.rozetka_pay_tokenization_title)
        )
        if (state.withCardName) {
            Spacer(modifier = Modifier.height(16.dp))
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_optional_card_name),
                value = state.cardName,
                onValueChange = {
                    onCardNameChanged(it)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                errorMessage = state.cardNameError,
                isError = state.cardNameError != null
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Subtitle(title = stringResource(id = R.string.rozetka_pay_form_card_info_title))
        Spacer(modifier = Modifier.height(10.dp))
        CardField(
            state = state.cardState,
            showCardholderNameField = state.withCardholderName,
            onStateChanged = { onCardFieldStateChanged(it) }
        )
        if (state.withEmail) {
            Spacer(modifier = Modifier.height(16.dp))
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_email),
                value = state.email,
                onValueChange = {
                    onEmailChanged(it)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None
                ),
                errorMessage = state.emailError,
                isError = state.emailError != null
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_tokenization_save_button),
            onClick = onSave
        )
        LegalBlock(
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
                displayState = DisplayState.Content,
            ),
            onEmailChanged = {},
            onCardNameChanged = {},
            onCardFieldStateChanged = {},
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
                displayState = DisplayState.Loading,
            ),
            onEmailChanged = {},
            onCardNameChanged = {},
            onCardFieldStateChanged = {},
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
                displayState = DisplayState.Error(
                    message = stringResource(id = R.string.rozetka_pay_tokenization_error_common)
                ),
            ),
            onEmailChanged = {},
            onCardNameChanged = {},
            onCardFieldStateChanged = {},
            onSave = {},
            onCancel = {},
            onFailed = {},
            onRetry = {}
        )
    }
}
