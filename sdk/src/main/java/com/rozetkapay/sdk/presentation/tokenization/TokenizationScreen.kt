package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.components.CloseButton
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.Title
import com.rozetkapay.sdk.presentation.components.inSheetPaddings
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun TokenizationScreen(
    state: TokenizationUiState,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .inSheetPaddings()
    ) {
        if (state.isInProgress) {
            LoadingScreen()
        } else {
            CloseButton(
                onClick = onCancel
            )
            TokenizationContent(
                state = state,
                onSuccess = onSuccess,
                onFailure = onFailure,
            )
        }
    }
}

@Composable
private fun TokenizationContent(
    state: TokenizationUiState,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Title(
            title = stringResource(id = R.string.rozetka_pay_tokenization_title)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSuccess
        ) {
            Text(text = "Simulate success")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onFailure
        ) {
            Text(text = "Simulate error")
        }
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
            onSuccess = {},
            onFailure = {},
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
            onSuccess = {},
            onFailure = {},
            onCancel = {}
        )
    }
}
