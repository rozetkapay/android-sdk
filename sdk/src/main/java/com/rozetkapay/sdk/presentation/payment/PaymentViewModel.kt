package com.rozetkapay.sdk.presentation.payment

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.contract.ApiTaskResult
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import com.rozetkapay.sdk.domain.RozetkaPayConfig
import com.rozetkapay.sdk.domain.errors.RozetkaPayTokenizationException
import com.rozetkapay.sdk.domain.models.ClientPayParameters
import com.rozetkapay.sdk.domain.models.Currency
import com.rozetkapay.sdk.domain.models.payment.BasePaymentParameters
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentResult
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.CardParsingResult
import com.rozetkapay.sdk.domain.usecases.CreatePaymentUseCase
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.presentation.components.CardFieldState
import com.rozetkapay.sdk.presentation.payment.googlepay.GooglePayInteractor
import com.rozetkapay.sdk.util.Logger
import com.rozetkapay.sdk.util.MoneyFormatter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

internal class PaymentViewModel(
    private val client: ClientPayParameters,
    private val parameters: PaymentParameters,
    private val resourcesProvider: ResourcesProvider,
    private val provideCardPaymentSystemUseCase: ProvideCardPaymentSystemUseCase,
    private val parseCardDataUseCase: ParseCardDataUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val googlePayInteractor: GooglePayInteractor?,
) : ViewModel() {

    private val _eventsChannel = Channel<PaymentEvent>()
    val eventsFlow = _eventsChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        PaymentUiState(
            allowTokenization = parameters.allowTokenization,
            amountWithCurrency = MoneyFormatter.formatCoinsToMoney(
                coins = parameters.amountParameters.amount,
                currency = Currency.getSymbol(parameters.amountParameters.currencyCode)
            ),
            allowGooglePay = googlePayInteractor != null,
            googlePayAllowedPaymentMethods = googlePayInteractor?.getAllowedPaymentMethods()?.toString() ?: ""
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        checkGooglePayParameters()
        viewModelScope.launch {
            verifyGooglePayReadiness()
        }
    }

    private suspend fun verifyGooglePayReadiness() {
        if (googlePayInteractor?.fetchCanUseGooglePay() == true) {
            _uiState.tryEmit(
                uiState.value.copy(
                    allowGooglePay = true,
                    googlePayAllowedPaymentMethods = googlePayInteractor.getAllowedPaymentMethods().toString()
                )
            )
        } else {
            Logger.d { "Google Pay is not available for this device" }
            _uiState.tryEmit(
                uiState.value.copy(
                    allowGooglePay = false,
                    googlePayAllowedPaymentMethods = ""
                )
            )
        }
    }

    fun onAction(action: PaymentAction) {
        when (action) {
            PaymentAction.Cancel -> cancelled()
            PaymentAction.Retry -> retry()
            is PaymentAction.UpdateCard -> updateCard(action.state)
            is PaymentAction.Failed -> failedDueToError(action.reason)
            PaymentAction.PayWithCard -> payWithCard()
            is PaymentAction.UpdateTokenization -> updateTokenization(action.value)
            is PaymentAction.GooglePayResult -> handleGooglePayResult(action.result)
            PaymentAction.PayWithGooglePay -> payWithGooglePay()
            is PaymentAction.PaymentConfirmed -> handlePaymentConfirmationResult(action.result)
        }
    }

    private fun payWithGooglePay() {
        googlePayInteractor?.preparePaymentTask(
            priceCoins = parameters.amountParameters.amount,
            currencyCode = parameters.amountParameters.currencyCode,
        )?.let { task ->
            loading()
            _eventsChannel.trySend(PaymentEvent.StartGooglePayPayment(task))
        }
    }

    private fun handleGooglePayResult(taskResult: ApiTaskResult<PaymentData>) {
        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val result: PaymentData? = taskResult.result
                if (result != null) {
                    Logger.d { "Google Pay result: ${result.toJson()}" }
                    val token = result.extractToken()
                    if (token != null) {
                        Logger.d { "Google Pay token: $token" }
                        runPaymentWithGooglePay(token)
                    } else {
                        showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay))
                    }
                } else {
                    Logger.e { "GooglePay result is null, but task result is success, this should never happen" }
                    showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay))
                }
            }

            CommonStatusCodes.CANCELED -> {
                Logger.i { "GooglePay process was cancelled by user" }
                retry()
            }

            AutoResolveHelper.RESULT_ERROR -> {
                Logger.e { "GooglePay process failed with error, status message = ${taskResult.status.statusMessage}" }
                showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay))
            }
        }
    }

    private fun runPaymentWithGooglePay(token: String) {
        loading()
        val paymentRequest = GooglePayPaymentRequest(
            clientParameters = client,
            paymentParameters = basePaymentParameters(),
            token = Base64.encode(token.toByteArray(), Base64.NO_PADDING).toString(Charsets.UTF_8)
        )
        createPaymentUseCase(paymentRequest)
            .catch { error ->
                Logger.e(throwable = error) { "Google pay payment error" }
                showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_common))
            }
            .onEach { result ->
                Logger.d { "Payment result: $result" }
                when (result) {
                    is CreatePaymentResult.Confirmation3DsRequired -> start3ds(
                        paymentId = result.paymentId,
                        url = result.url
                    )

                    is CreatePaymentResult.Error -> showError(
                        message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common)
                    )

                    is CreatePaymentResult.Success -> success(
                        paymentId = result.paymentId,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun start3ds(
        paymentId: String,
        url: String,
    ) {
        loading()
        _eventsChannel.trySend(
            PaymentEvent.Start3dsConfirmation(
                paymentId = paymentId,
                url = url
            )
        )
    }

    private fun handlePaymentConfirmationResult(result: ConfirmPaymentResult) {
        Logger.d { "Payment confirmation result $result" }
        when (result) {
            is ConfirmPaymentResult.Success -> success(result.paymentId)

            ConfirmPaymentResult.Cancelled -> retry()

            is ConfirmPaymentResult.Error -> {
                showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_common))
            }
        }
    }

    private fun PaymentData.extractToken(): String? {
        return try {
            JSONObject(this.toJson())
                .getJSONObject("paymentMethodData")
                .getJSONObject("tokenizationData")
                .getString("token")
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Failed to extract token from Google Pay result" }
            null
        }
    }

    private fun payWithCard() {
        val cardState = validateCardState(uiState.value.cardState)
        if (cardState.hasErrors) {
            _uiState.tryEmit(
                uiState.value.copy(
                    cardState = cardState
                )
            )
        } else {
            runPaymentWithCard(
                cardState = cardState,
            )
        }
    }

    private fun runPaymentWithCard(cardState: CardFieldState) {
        // payment with card data not supported yet
        // it will be implemented in the future releases
        showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_unsupported))
    }

    private fun basePaymentParameters() = BasePaymentParameters(
        amount = parameters.amountParameters.amount,
        currencyCode = parameters.amountParameters.currencyCode,
        orderId = parameters.orderId
    )

    private fun updateCard(state: CardFieldState) {
        val newState = if (uiState.value.cardState.hasErrors) {
            validateCardState(state)
        } else {
            state
        }
        _uiState.tryEmit(
            uiState.value.copy(
                cardState = newState.copy(
                    paymentSystem = provideCardPaymentSystemUseCase.invoke(state.cardNumber)
                )
            )
        )
    }

    private fun updateTokenization(value: Boolean) {
        _uiState.tryEmit(
            uiState.value.copy(
                tokenize = value
            )
        )
    }

    private fun validateCardState(state: CardFieldState): CardFieldState {
        val result = parseCardDataUseCase(
            rawCardNumber = state.cardNumber,
            rawCvv = state.cvv,
            rawExpDate = state.expDate,
            isCardholderNameRequired = false,
            rawCardholderName = state.cardholderName
        )
        return if (result is CardParsingResult.Error) {
            return state.copy(
                cardNumberError = result.cardNumberError,
                cvvError = result.cvvError,
                expDateError = result.expDateError,
                cardholderNameError = result.cardholderNameError
            )
        } else {
            state.copy(
                cardNumberError = null,
                cvvError = null,
                expDateError = null,
                cardholderNameError = null
            )
        }
    }

    private fun loading() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = PaymentDisplayState.Loading
            )
        )
    }

    private fun success(
        paymentId: String,
    ) {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Complete(
                    paymentId = paymentId,
                    orderId = parameters.orderId
                )
            )
        )
    }

    private fun showError(
        message: String,
    ) {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = PaymentDisplayState.Error(
                    message = message
                )
            )
        )
    }

    private fun retry() {
        _uiState.tryEmit(
            uiState.value.copy(
                displayState = PaymentDisplayState.Content
            )
        )
    }

    private fun cancelled() {
        _eventsChannel.trySend(
            PaymentEvent.Result(PaymentResult.Cancelled)
        )
    }

    private fun failedDueToError(reason: Throwable? = null) {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Failed(
                    message = if (reason is RozetkaPayTokenizationException) reason.errorMessage else null,
                    error = reason
                )
            )
        )
    }

    private fun checkGooglePayParameters() {
        if (parameters.googlePayConfig is GooglePayConfig.Test) {
            Log.w(
                Logger.DEFAULT_TAG,
                """
                ⚠️ WARNING: GOOGLE PAY IS CONFIGURED IN TEST MODE! ⚠️
                ⚠️ THIS IS A DEVELOPMENT CONFIGURATION AND SHOULD NOT BE USED IN PRODUCTION. ⚠️
                DETAILS:
                - Gateway: ${parameters.googlePayConfig.gateway}
                - Merchant ID: ${parameters.googlePayConfig.merchantId}
                
                Please ensure this configuration is switched to Production mode before releasing the app.
                """.trimIndent()
            )
        }
    }

    internal class Factory(
        private val parametersSupplier: () -> PaymentSheetContract.Parameters,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras,
        ): T {
            val parameters = parametersSupplier()
            return PaymentViewModel(
                client = parameters.client,
                parameters = parameters.parameters,
                createPaymentUseCase = RozetkaPayKoinContext.koin.get(),
                resourcesProvider = RozetkaPayKoinContext.koin.get(),
                provideCardPaymentSystemUseCase = RozetkaPayKoinContext.koin.get(),
                parseCardDataUseCase = RozetkaPayKoinContext.koin.get(),
                googlePayInteractor = parameters.parameters.googlePayConfig?.let { googlePayConfig ->
                    GooglePayInteractor(
                        applicationContext = RozetkaPayKoinContext.koin.get(),
                        gateway = googlePayConfig.gateway,
                        merchantId = googlePayConfig.merchantId,
                        merchantName = googlePayConfig.merchantName,
                        countryCode = RozetkaPayConfig.GOOGLE_PAY_COUNTRY_CODE,
                        isTestEnvironment = googlePayConfig is GooglePayConfig.Test,
                    )
                }
            ) as T
        }
    }
}