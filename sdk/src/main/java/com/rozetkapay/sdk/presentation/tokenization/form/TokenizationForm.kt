package com.rozetkapay.sdk.presentation.tokenization.form

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.CardExpDate
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.DeviceInfo
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationFormParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
import com.rozetkapay.sdk.domain.usecases.GetDeviceInfoUseCase
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.domain.validators.ValidationResult
import com.rozetkapay.sdk.domain.validators.Validator
import com.rozetkapay.sdk.presentation.components.ErrorScreen
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.components.RozetkaPayContext
import com.rozetkapay.sdk.presentation.forms.card.CardFormAction
import com.rozetkapay.sdk.presentation.forms.card.CardFormViewModel
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.tokenization.TokenizationAction
import com.rozetkapay.sdk.presentation.tokenization.TokenizationContent
import com.rozetkapay.sdk.presentation.tokenization.TokenizationDisplayState
import com.rozetkapay.sdk.presentation.tokenization.TokenizationViewModel
import com.rozetkapay.sdk.presentation.tokenization.rememberDefaultTokenizationStringResourcesProvider
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TokenizationForm(
    client: ClientWidgetParameters,
    parameters: TokenizationFormParameters = TokenizationFormParameters(),
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    cardFormFooterContent: @Composable (() -> Unit)? = null,
    onResult: (TokenizationResult) -> Unit,
) = RozetkaPayContext {
    RozetkaPayTheme(
        themeConfigurator = themeConfigurator
    ) {
        val isPreview: Boolean = LocalInspectionMode.current
        val viewModel: TokenizationViewModel = if (isPreview) {
            previewTokenizationViewModel(
                client = client,
                context = LocalContext.current,
            )
        } else {
            koinViewModel {
                parametersOf(client)
            }
        }
        val cardFormViewModel: CardFormViewModel = if (isPreview) {
            previewCardFormViewModel(parameters.cardFieldsParameters)
        } else {
            koinViewModel {
                parametersOf(parameters.cardFieldsParameters)
            }
        }
        LaunchedEffect(Unit) {
            viewModel.resultStateFlow.collect { result ->
                onResult(result)
                cardFormViewModel.onAction(CardFormAction.ClearForm)
            }
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()
        val stringResourcesProvider = parameters.stringResourcesProvider
            ?: rememberDefaultTokenizationStringResourcesProvider()
        when (val displayState = state.displayState) {
            TokenizationDisplayState.Content -> {
                TokenizationContent(
                    withTitle = false,
                    withCardTitle = parameters.showCardFormTitle,
                    withLegalBlock = parameters.showLegalBlock,
                    cardFormViewModel = cardFormViewModel,
                    stingResourcesProvider = stringResourcesProvider,
                    cardFormFooterContent = cardFormFooterContent,
                    onSave = { cardData -> viewModel.onAction(TokenizationAction.Save(cardData = cardData)) },
                )
            }

            is TokenizationDisplayState.Error -> {
                ErrorScreen(
                    message = displayState.message,
                    onRetry = { viewModel.onAction(TokenizationAction.Retry) },
                    onCancel = { viewModel.onAction(TokenizationAction.Failed(displayState.reason)) }
                )
            }

            TokenizationDisplayState.Loading -> {
                LoadingScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true, uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun TokenizationFormPreview() {
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        var isSaveCardChecked by remember { mutableStateOf(false) }
        TokenizationForm(
            client = ClientWidgetParameters(widgetKey = "preview_widget_key"),
            parameters = TokenizationFormParameters(
                cardFieldsParameters = CardFieldsParameters(
                    cardNameField = FieldRequirement.Optional,
                    emailField = FieldRequirement.Optional,
                    cardholderNameField = FieldRequirement.Optional,
                ),
                showCardFormTitle = false,
                showLegalBlock = false,
            ),
            onResult = {},
            cardFormFooterContent = {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSaveCardChecked,
                        onCheckedChange = {
                            isSaveCardChecked = it
                        }
                    )
                    Text(
                        text = "Save card",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

private fun previewTokenizationViewModel(
    client: ClientWidgetParameters,
    context: Context,
) = TokenizationViewModel(
    client = client,
    tokenizeCardUseCase = TokenizeCardUseCase(
        tokenizationRepository = object : TokenizationRepository {
            override suspend fun tokenizeCard(
                widgetKey: String,
                cardData: CardData,
                device: DeviceInfo,
            ): TokenizedCard {
                return TokenizedCard(
                    token = "preview_token"
                )
            }
        },
        getDeviceInfoUseCase = GetDeviceInfoUseCase(context = context),
        provideCardPaymentSystemUseCase = ProvideCardPaymentSystemUseCase(),
    ),
    resourcesProvider = object : ResourcesProvider {
        override fun getString(stringResId: Int, vararg formatArgs: Any): String {
            return "preview string"
        }
    },
)

private fun previewCardFormViewModel(
    parameters: CardFieldsParameters,
) = CardFormViewModel(
    parameters = parameters,
    parseCardDataUseCase = ParseCardDataUseCase(
        cardNumberValidator = object : Validator<String>() {
            override fun validate(value: String): ValidationResult {
                return ValidationResult.Valid
            }
        },

        cvvValidator = object : Validator<String>() {
            override fun validate(value: String): ValidationResult {
                return ValidationResult.Valid
            }
        },
        expDateValidator = object : Validator<CardExpDate>() {
            override fun validate(value: CardExpDate): ValidationResult {
                return ValidationResult.Valid
            }
        },
        cardholderNameValidator = object : Validator<String>() {
            override fun validate(value: String): ValidationResult {
                return ValidationResult.Valid
            }
        },
        emailValidator = object : Validator<String>() {
            override fun validate(value: String): ValidationResult {
                return ValidationResult.Valid
            }
        },
        cardNameValidator = object : Validator<String>() {
            override fun validate(value: String): ValidationResult {
                return ValidationResult.Valid
            }
        },
        resourcesProvider = object : ResourcesProvider {
            override fun getString(stringResId: Int, vararg formatArgs: Any): String {
                return "preview string"
            }
        },
    ),
    provideCardPaymentSystemUseCase = ProvideCardPaymentSystemUseCase()
)