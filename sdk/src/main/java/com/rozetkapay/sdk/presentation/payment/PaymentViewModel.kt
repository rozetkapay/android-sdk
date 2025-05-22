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
import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.errors.RozetkaPayTokenizationException
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.Currency
import com.rozetkapay.sdk.domain.models.payment.BasePaymentParameters
import com.rozetkapay.sdk.domain.models.payment.CardTokenPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentResult
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.domain.models.payment.PaymentStatus
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.domain.models.payment.SingleTokenPayment
import com.rozetkapay.sdk.domain.models.tokenization.TokenizedCard
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.usecases.CheckPaymentStatusUseCase
import com.rozetkapay.sdk.domain.usecases.CreatePaymentUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
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

internal class PaymentViewModel(
    private val clientAuthParameters: ClientAuthParameters,
    private val parameters: PaymentParameters,
    private val resourcesProvider: ResourcesProvider,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val checkPaymentStatusUseCase: CheckPaymentStatusUseCase,
    private val googlePayInteractor: GooglePayInteractor?,
    private val tokenizeCardUseCase: TokenizeCardUseCase,
) : ViewModel() {

    private val _eventsChannel = Channel<PaymentEvent>()
    val eventsFlow = _eventsChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(
        PaymentUiState(
            displayState = PaymentDisplayState.Empty,
            amountWithCurrency = MoneyFormatter.formatCoinsToMoney(
                coins = parameters.amountParameters.amount,
                currency = Currency.getSymbol(parameters.amountParameters.currencyCode)
            ),
            allowGooglePay = googlePayInteractor != null,
            googlePayAllowedPaymentMethods = googlePayInteractor?.getAllowedPaymentMethods()?.toString() ?: ""
        )
    )
    val uiState = _uiState.asStateFlow()

    // paymentId -> tokenized card
    private val tokensStorage = HashMap<String, TokenizedCard>()

    init {
        retry()
        if (parameters.paymentType is RegularPayment) {
            checkGooglePayParameters()
            viewModelScope.launch {
                verifyGooglePayReadiness()
            }
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
            is PaymentAction.Failed -> failedDueToError(action.reason)
            is PaymentAction.PayWithCard -> payWithCard(action.cardData)
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
                    val token = googlePayInteractor?.extractToken(result)
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
            authParameters = clientAuthParameters,
            paymentParameters = basePaymentParameters(),
            googlePayToken = Base64.encode(token.toByteArray(), Base64.NO_WRAP).toString(Charsets.UTF_8)
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
            is ConfirmPaymentResult.Completed -> {
                recheckPaymentStatus(result.paymentId)
            }

            // payment status should be recheck in case of cancellation
            // because this cancellation can be caused by user action
            // on the moment when payment confirmation request already sent
            is ConfirmPaymentResult.Cancelled -> {
                recheckPaymentStatus(result.paymentId)
            }

            is ConfirmPaymentResult.Success -> success(result.paymentId)

            is ConfirmPaymentResult.Error -> {
                showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_common))
            }
        }
    }

    private fun payWithCard(cardData: CardData) {
        loading()
        tokenizeCardUseCase(
            TokenizeCardUseCase.Parameters(
                cardData = cardData,
                widgetKey = clientAuthParameters.widgetKey,
            )
        ).catch { error ->
            Logger.e(throwable = error) { "Tokenization error, cant tokenize card for payment" }
            showError(
                message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common)
            )
        }.onEach { tokenizedCard ->
            payWithCardToken(
                tokenizedCard = tokenizedCard
            )
        }.launchIn(viewModelScope)
    }

    private fun payWithCardToken(tokenizedCard: TokenizedCard) {
        loading()
        val paymentRequest = CardTokenPaymentRequest(
            authParameters = clientAuthParameters,
            paymentParameters = basePaymentParameters(),
            cardToken = tokenizedCard.token
        )
        createPaymentUseCase(
            params = paymentRequest
        ).catch { error ->
            Logger.e(throwable = error) { "Card token payment error" }
            showError(resourcesProvider.getString(R.string.rozetka_pay_payment_error_common))
        }.onEach { result ->
            Logger.d { "Payment result: $result" }
            when (result) {
                is CreatePaymentResult.Confirmation3DsRequired -> {
                    tokensStorage[result.paymentId] = tokenizedCard
                    start3ds(
                        paymentId = result.paymentId,
                        url = result.url
                    )
                }

                is CreatePaymentResult.Error -> showError(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common)
                )

                is CreatePaymentResult.Success -> {
                    tokensStorage[result.paymentId] = tokenizedCard
                    success(
                        paymentId = result.paymentId,
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun basePaymentParameters() = BasePaymentParameters(
        amount = parameters.amountParameters.amount,
        currencyCode = parameters.amountParameters.currencyCode,
        orderId = parameters.orderId,
        callbackUrl = parameters.callbackUrl
    )

    private fun recheckPaymentStatus(paymentId: String) {
        checkPaymentStatusUseCase(
            CheckPaymentStatusUseCase.Parameters(
                paymentId = paymentId,
                orderId = parameters.orderId,
                authParameters = clientAuthParameters
            )
        ).catch { error ->
            Logger.e(throwable = error) { "Check payment status error" }
            completedPending(paymentId = paymentId)
        }.onEach { paymentData ->
            Logger.d { "Recheck payment data: $paymentData" }
            when (paymentData.status) {
                PaymentStatus.Success -> success(paymentId = paymentId)

                PaymentStatus.Failure -> failed(
                    paymentId = paymentId,
                    message = paymentData.statusDescription,
                    type = paymentData.statusCode
                )

                PaymentStatus.Init,
                PaymentStatus.Pending,
                    -> completedPending(paymentId = paymentId)
            }
        }.launchIn(viewModelScope)
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
        val tokenizedCard = if (isTokenizationAllowed()) tokensStorage[paymentId] else null
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Complete(
                    paymentId = paymentId,
                    orderId = parameters.orderId,
                    tokenizedCard = tokenizedCard
                )
            )
        )
    }

    private fun isTokenizationAllowed(): Boolean {
        return (parameters.paymentType as? RegularPayment)?.allowTokenization ?: false
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
        tokensStorage.clear()
        when (parameters.paymentType) {
            is RegularPayment -> {
                _uiState.tryEmit(
                    uiState.value.copy(
                        displayState = PaymentDisplayState.Content,
                    )
                )
            }

            is SingleTokenPayment -> {
                payWithCardToken(
                    tokenizedCard = TokenizedCard(
                        token = parameters.paymentType.token
                    )
                )
            }
        }
    }

    private fun completedPending(
        paymentId: String,
    ) {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Pending(
                    orderId = parameters.orderId,
                    paymentId = paymentId
                )
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

    private fun failed(
        paymentId: String,
        message: String?,
        type: String?,
    ) {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Failed(
                    paymentId = paymentId,
                    message = message,
                    error = RozetkaPayPaymentException(
                        code = "failure",
                        type = type,
                        errorMessage = message ?: ""
                    )
                )
            )
        )
    }

    private fun checkGooglePayParameters() {
        val googlePayConfig = (parameters.paymentType as? RegularPayment)?.googlePayConfig
        if (googlePayConfig is GooglePayConfig.Test) {
            Log.w(
                Logger.DEFAULT_TAG,
                """
                ⚠️ WARNING: GOOGLE PAY IS CONFIGURED IN TEST MODE! ⚠️
                ⚠️ THIS IS A DEVELOPMENT CONFIGURATION AND SHOULD NOT BE USED IN PRODUCTION. ⚠️
                DETAILS:
                - Gateway: ${googlePayConfig.gateway}
                - Merchant ID: ${googlePayConfig.merchantId}
                
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
            val paymentParameters = parameters.parameters
            return PaymentViewModel(
                clientAuthParameters = parameters.clientAuthParameters,
                parameters = paymentParameters,
                createPaymentUseCase = RozetkaPayKoinContext.koin.get(),
                checkPaymentStatusUseCase = RozetkaPayKoinContext.koin.get(),
                resourcesProvider = RozetkaPayKoinContext.koin.get(),
                tokenizeCardUseCase = RozetkaPayKoinContext.koin.get(),
                googlePayInteractor = (paymentParameters.paymentType as? RegularPayment)?.googlePayConfig?.let { googlePayConfig ->
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