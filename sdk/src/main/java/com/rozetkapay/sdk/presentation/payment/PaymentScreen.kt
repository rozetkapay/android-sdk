package com.rozetkapay.sdk.presentation.payment

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.components.CardField
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.presentation.components.ErrorScreen
import com.rozetkapay.sdk.presentation.components.LegalIconsBlock
import com.rozetkapay.sdk.presentation.components.LegalTextBlock
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.PrimaryButton
import com.rozetkapay.sdk.presentation.components.PrimaryCheckbox
import com.rozetkapay.sdk.presentation.components.SheetCloseHeader
import com.rozetkapay.sdk.presentation.components.Subtitle
import com.rozetkapay.sdk.presentation.components.Title
import com.rozetkapay.sdk.presentation.components.inSheetPaddings
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun PaymentScreen(
    state: PaymentUiState,
    onCardFieldStateChanged: (CardFieldState) -> Unit,
    onTokenizationChanged: (Boolean) -> Unit,
    onPay: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onFailed: (reason: Throwable?) -> Unit,
) {
    Column(
        modifier = Modifier
            .animateContentSize()
            .verticalScroll(rememberScrollState())
            .inSheetPaddings()
    ) {
        when (state.displayState) {
            PaymentDisplayState.Content -> {
                SheetCloseHeader(
                    onClose = onCancel
                )
                PaymentContent(
                    state = state,
                    onPay = onPay,
                    onTokenizationChanged = onTokenizationChanged,
                    onCardFieldStateChanged = onCardFieldStateChanged
                )
            }

            is PaymentDisplayState.Error -> {
                ErrorScreen(
                    message = state.displayState.message,
                    onRetry = onRetry,
                    onCancel = {
                        onFailed(state.displayState.reason)
                    }
                )
            }

            PaymentDisplayState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

@Composable
private fun PaymentContent(
    state: PaymentUiState,
    onPay: () -> Unit,
    onCardFieldStateChanged: (CardFieldState) -> Unit,
    onTokenizationChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Title(
            title = stringResource(id = R.string.rozetka_pay_payment_title)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Subtitle(title = stringResource(id = R.string.rozetka_pay_form_card_info_title))
        Spacer(modifier = Modifier.height(10.dp))
        CardField(
            state = state.cardState,
            showCardholderNameField = state.withCardholderName,
            onStateChanged = { onCardFieldStateChanged(it) }
        )
        if (state.allowTokenization) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTokenizationChanged(!state.tokenize) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryCheckbox(
                    checked = state.tokenize,
                    onCheckedChange = onTokenizationChanged
                )
                Text(
                    text = stringResource(id = R.string.rozetka_pay_form_save_card),
                    style = DomainTheme.typography.labelSmall,
                    color = DomainTheme.colors.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_payment_pay_button, state.amountWithCurrency),
            onClick = onPay
        )
        LegalIconsBlock(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        )
        LegalTextBlock(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentScreenPreview() {
    RozetkaPayTheme {
        PaymentScreen(
            state = PaymentUiState(
                displayState = PaymentDisplayState.Content,
                allowTokenization = true,
            ),
            onPay = {},
            onCardFieldStateChanged = {},
            onTokenizationChanged = {},
            onCancel = {},
            onRetry = {},
            onFailed = {}
        )
    }
}