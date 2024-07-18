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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.components.CardField
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.presentation.components.FormTextField
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
    onNameChanged: (String) -> Unit,
    onCardFieldStateChanged: (CardFieldState) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .verticalScroll(rememberScrollState())
            .inSheetPaddings()
    ) {
        if (state.isInProgress) {
            LoadingScreen()
        } else {
            SheetCloseHeader(
                onClose = onCancel
            )
            TokenizationContent(
                state = state,
                onSave = onSave,
                onNameChanged = onNameChanged,
                onCardFieldStateChanged = onCardFieldStateChanged
            )
        }
    }
}

@Composable
private fun TokenizationContent(
    state: TokenizationUiState,
    onNameChanged: (String) -> Unit,
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
        if (state.withName) {
            Spacer(modifier = Modifier.height(16.dp))
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_optional_card_name),
                value = state.cardName,
                onValueChange = {
                    onNameChanged(it)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Subtitle(title = stringResource(id = R.string.rozetka_pay_form_card_info_title))
        Spacer(modifier = Modifier.height(10.dp))
        CardField(
            state = state.cardState,
            onStateChanged = { onCardFieldStateChanged(it) }
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Save",
            onClick = onSave
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentPreview() {
    RozetkaPayTheme {
        TokenizationScreen(
            state = TokenizationUiState(
                isInProgress = false
            ),
            onNameChanged = {},
            onCardFieldStateChanged = {},
            onSave = {},
            onCancel = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TokenizationContentProgressPreview() {
    RozetkaPayTheme {
        TokenizationScreen(
            state = TokenizationUiState(
                isInProgress = true
            ),
            onNameChanged = {},
            onCardFieldStateChanged = {},
            onSave = {},
            onCancel = {}
        )
    }
}
