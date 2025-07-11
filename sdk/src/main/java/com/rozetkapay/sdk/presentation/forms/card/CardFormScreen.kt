package com.rozetkapay.sdk.presentation.forms.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.validators.ValidationResult
import com.rozetkapay.sdk.domain.validators.Validator
import com.rozetkapay.sdk.presentation.components.FormTextField
import com.rozetkapay.sdk.presentation.components.Subtitle
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun CardFormScreen(
    viewModel: CardFormViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CardFormScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun CardFormScreenContent(
    state: CardFormUiState,
    onAction: (CardFormAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (state.withCardName) {
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_optional_card_name),
                value = state.cardName,
                onValueChange = {
                    onAction(CardFormAction.UpdateCardName(it))
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                errorMessage = state.cardNameError,
                isError = state.cardNameError != null
            )
            Spacer(modifier = Modifier.height(28.dp))
        }
        Subtitle(title = stringResource(id = R.string.rozetka_pay_form_card_info_title))
        Spacer(modifier = Modifier.height(10.dp))
        CardField(
            state = state.cardState,
            showCardholderNameField = state.withCardholderName,
            onStateChanged = {
                onAction(CardFormAction.UpdateCard(it))
            }
        )
        if (state.withEmail) {
            Spacer(modifier = Modifier.height(16.dp))
            FormTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.rozetka_pay_form_email),
                value = state.email,
                onValueChange = {
                    onAction(CardFormAction.UpdateEmail(it))
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
    }
}

private fun <T> mockValidator(): Validator<T> {
    return object : Validator<T>() {
        override fun validate(value: T): ValidationResult {
            return ValidationResult.Valid
        }
    }
}

internal val MOCK_CARD_FORM_VIEWMODEL = CardFormViewModel(
    parameters = CardFieldsParameters(
        cardNameField = FieldRequirement.None,
        emailField = FieldRequirement.Optional,
        cardholderNameField = FieldRequirement.Optional,
    ),
    parseCardDataUseCase = ParseCardDataUseCase(
        cardNumberValidator = mockValidator(),
        cvvValidator = mockValidator(),
        expDateValidator = mockValidator(),
        cardNameValidator = mockValidator(),
        emailValidator = mockValidator(),
        cardholderNameValidator = mockValidator(),
        resourcesProvider = object : ResourcesProvider {
            override fun getString(stringResId: Int, vararg formatArgs: Any): String {
                return "string"
            }
        }
    ),
    provideCardPaymentSystemUseCase = ProvideCardPaymentSystemUseCase(),
)

@Composable
@Preview(showBackground = true)
private fun CardFormScreenPreview() {
    RozetkaPayTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CardFormScreen(
                viewModel = MOCK_CARD_FORM_VIEWMODEL
            )
        }
    }
}