package com.rozetkapay.sdk.presentation.payment

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.presentation.components.ErrorScreen
import com.rozetkapay.sdk.presentation.components.LegalIconsBlock
import com.rozetkapay.sdk.presentation.components.LegalTextBlock
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.PrimaryButton
import com.rozetkapay.sdk.presentation.components.SheetCloseHeader
import com.rozetkapay.sdk.presentation.components.Title
import com.rozetkapay.sdk.presentation.components.inSheetPaddings
import com.rozetkapay.sdk.presentation.forms.card.CardFormScreen
import com.rozetkapay.sdk.presentation.forms.card.CardFormViewModel
import com.rozetkapay.sdk.presentation.forms.card.MOCK_CARD_FORM_VIEWMODEL
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun PaymentScreen(
    state: PaymentUiState,
    cardFormViewModel: CardFormViewModel,
    onAction: (PaymentAction) -> Unit,
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
                    onClose = { onAction(PaymentAction.Cancel) }
                )
                PaymentContent(
                    state = state,
                    cardFormViewModel = cardFormViewModel,
                    onPayWithCard = { onAction(PaymentAction.PayWithCard(cardData = it)) },
                    onGooglePay = { onAction(PaymentAction.PayWithGooglePay) },
                )
            }

            is PaymentDisplayState.Error -> {
                ErrorScreen(
                    message = state.displayState.message,
                    onRetry = { onAction(PaymentAction.Retry) },
                    onCancel = {
                        onAction(
                            PaymentAction.Failed(
                                reason = state.displayState.reason
                            )
                        )
                    }
                )
            }

            PaymentDisplayState.Loading -> {
                LoadingScreen()
            }

            PaymentDisplayState.Empty -> {
                // show empty state
            }
        }
    }
}

@Composable
private fun PaymentContent(
    state: PaymentUiState,
    cardFormViewModel: CardFormViewModel,
    onPayWithCard: (CardData) -> Unit,
    onGooglePay: () -> Unit,
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
        if (state.allowGooglePay) {
            PayButton(
                modifier = Modifier
                    .testTag("googlePayButton")
                    .fillMaxWidth(),
                onClick = onGooglePay,
                type = ButtonType.Buy,
                theme = if (isSystemInDarkTheme()) ButtonTheme.Light else ButtonTheme.Dark,
                radius = DomainTheme.sizes.buttonCornerRadius,
                allowedPaymentMethods = state.googlePayAllowedPaymentMethods
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = DomainTheme.colors.componentDivider
                )
                Text(
                    text = stringResource(id = R.string.rozetka_pay_payment_label_use_card),
                    style = DomainTheme.typography.labelSmall,
                    color = DomainTheme.colors.subtitle
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = DomainTheme.colors.componentDivider
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        CardFormScreen(
            viewModel = cardFormViewModel
        )
        Spacer(modifier = Modifier.height(40.dp))
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_payment_pay_button, state.amountWithCurrency),
            onClick = {
                val result = cardFormViewModel.parseCardData()
                if (result is CardParsingResult.Success) {
                    onPayWithCard(result.cardData)
                }
            }
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
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true, uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PaymentScreenPreview() {
    RozetkaPayTheme {
        PaymentScreen(
            state = PaymentUiState(
                displayState = PaymentDisplayState.Content,
                allowGooglePay = true
            ),
            cardFormViewModel = MOCK_CARD_FORM_VIEWMODEL,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true, uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun PaymentScreenSmallPreview() {
    RozetkaPayTheme {
        PaymentScreen(
            state = PaymentUiState(
                displayState = PaymentDisplayState.Content,
                allowGooglePay = false
            ),
            cardFormViewModel = MOCK_CARD_FORM_VIEWMODEL,
            onAction = {}
        )
    }
}