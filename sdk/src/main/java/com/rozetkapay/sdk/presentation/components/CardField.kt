package com.rozetkapay.sdk.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.util.masks.CardNumberMask
import com.rozetkapay.sdk.presentation.util.masks.ExpirationDateMask

@Composable
internal fun CardField(
    modifier: Modifier = Modifier,
    showCardholderNameField: Boolean,
    state: CardFieldState,
    onStateChanged: (newState: CardFieldState) -> Unit,
) {
    Column(
        modifier = modifier
            .animateContentSize()
    ) {
        FormTextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(id = R.string.rozetka_pay_form_card_number),
            value = state.cardNumber,
            onValueChange = {
                if (it.isDigitsOnly()) {
                    onStateChanged(
                        state.copy(
                            cardNumber = it.take(CardNumberMask.MAX_CREDIT_CARD_NUMBER_LENGTH)
                        )
                    )
                }
            },
            isError = state.isCardNumberError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(
                topStart = DomainTheme.sizes.componentCornerRadius,
                topEnd = DomainTheme.sizes.componentCornerRadius
            ),
            visualTransformation = CardNumberMask(),
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .height(22.dp)
                        .width(36.dp)
                        .padding(end = 6.dp),
                    painter = painterResource(id = state.paymentSystem.iconRes()),
                    contentDescription = "payment-system-icon"
                )
            }
        )
        HorizontalDivider(
            thickness = DomainTheme.sizes.borderWidth,
            color = DomainTheme.colors.componentDivider,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            FormTextField(
                modifier = Modifier.weight(1f),
                placeholder = stringResource(id = R.string.rozetka_pay_form_exp_date),
                value = state.expDate,
                isError = state.isExpDateError,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        onStateChanged(state.copy(expDate = it.take(4)))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(
                    bottomStart = DomainTheme.sizes.componentCornerRadius,
                ),
                visualTransformation = ExpirationDateMask()
            )
            VerticalDivider(
                thickness = DomainTheme.sizes.borderWidth,
                color = DomainTheme.colors.componentDivider,
            )
            FormTextField(
                modifier = Modifier.weight(1f),
                placeholder = stringResource(id = R.string.rozetka_pay_form_cvv),
                value = state.cvv,
                isError = state.isCvvError,
                onValueChange = {
                    if (it.isDigitsOnly()) {
                        onStateChanged(state.copy(cvv = it.take(3)))
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(
                    bottomEnd = DomainTheme.sizes.componentCornerRadius,
                )
            )
        }

        if (state.isCardNumberError || state.isExpDateError || state.isCvvError) {
            Text(
                modifier = Modifier.padding(
                    start = 14.dp,
                    top = 10.dp
                ),
                text = state.cardNumberError ?: state.expDateError ?: state.cvvError ?: "",
                color = DomainTheme.colors.error,
                style = DomainTheme.typography.labelSmall,
            )
        }

        if (showCardholderNameField) {
            Spacer(modifier = Modifier.height(16.dp))
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_cardholder_name),
                value = state.cardholderName,
                onValueChange = {
                    onStateChanged(state.copy(cardholderName = it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                isError = state.isCardholderNameError,
                errorMessage = state.cardholderNameError
            )
        }
    }
}

internal data class CardFieldState(
    val cardNumber: String = "",
    val cardNumberError: String? = null,
    val cvv: String = "",
    val cvvError: String? = null,
    val expDate: String = "",
    val expDateError: String? = null,
    val cardholderName: String = "",
    val cardholderNameError: String? = null,
    val paymentSystem: PaymentSystem? = null,
) {
    val isCardNumberError: Boolean = cardNumberError != null
    val isCvvError: Boolean = cvvError != null
    val isExpDateError: Boolean = expDateError != null
    val isCardholderNameError: Boolean = cardholderNameError != null
    val hasErrors: Boolean = isCardNumberError || isCvvError || isExpDateError || isCardholderNameError
}

@DrawableRes
private fun PaymentSystem?.iconRes(): Int {
    return when (this) {
        PaymentSystem.Visa -> R.drawable.rozetka_pay_ic_visa
        PaymentSystem.MasterCard -> R.drawable.rozetka_pay_ic_mastercard
        PaymentSystem.Prostir -> R.drawable.rozetka_pay_ic_prostir
        else -> R.drawable.rozetka_pay_ic_card_default
    }
}

@Composable
@Preview(showBackground = true)
private fun CardFieldPreview() {
    RozetkaPayTheme {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            CardField(
                state = CardFieldState(),
                showCardholderNameField = true,
                onStateChanged = { }
            )
            CardField(
                state = CardFieldState(
                    cardNumber = "1234567812345678",
                    cvv = "123",
                    expDate = "1234",
                ),
                showCardholderNameField = true,
                onStateChanged = { }
            )
            CardField(
                state = CardFieldState(
                    cardNumber = "1234567812345678",
                    cardNumberError = "Error message",
                    cvv = "123",
                    expDateError = "Error message",
                    expDate = "1234",
                    cvvError = "Error message",
                ),
                showCardholderNameField = true,
                onStateChanged = { }
            )
        }
    }
}