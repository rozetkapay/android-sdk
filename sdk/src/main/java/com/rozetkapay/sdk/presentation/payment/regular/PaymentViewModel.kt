package com.rozetkapay.sdk.presentation.payment.regular

import android.util.Base64
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
import com.rozetkapay.sdk.domain.errors.RozetkaPayException
import com.rozetkapay.sdk.domain.errors.RozetkaPayPaymentException
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.Currency
import com.rozetkapay.sdk.domain.models.RozetkaPayEnvironment
import com.rozetkapay.sdk.domain.models.payment.CardTokenPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.domain.models.payment.CreatePaymentResult
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.domain.models.payment.GooglePayPaymentRequest
import com.rozetkapay.sdk.domain.models.payment.PaymentDetails
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
import com.rozetkapay.sdk.presentation.payment.PaymentAction
import com.rozetkapay.sdk.presentation.payment.PaymentBottomSheetViewModel
import com.rozetkapay.sdk.presentation.payment.PaymentDisplayState
import com.rozetkapay.sdk.presentation.payment.PaymentUiState
import com.rozetkapay.sdk.presentation.payment.googlepay.GooglePayInteractor
import com.rozetkapay.sdk.util.Logger
import com.rozetkapay.sdk.util.MoneyFormatter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    googlePayInteractor: GooglePayInteractor?,
    private val tokenizeCardUseCase: TokenizeCardUseCase,
    private val environment: RozetkaPayEnvironment,
) : PaymentBottomSheetViewModel(
    googlePayInteractor = googlePayInteractor,
) {

    private val _eventsChannel = Channel<PaymentEvent>()
    val eventsFlow: Flow<PaymentEvent> = _eventsChannel.receiveAsFlow()

    override val _uiState = MutableStateFlow(
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
    override val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var lastPaymentTokenizedCard: TokenizedCard? = null
    private var lastPaymentId: String? = null

    init {
        retry()
        if (parameters.paymentType is RegularPayment) {
            checkGooglePayParameters(parameters.paymentType.googlePayConfig)
            viewModelScope.launch {
                verifyGooglePayReadiness()
            }
        }
    }

    private fun clearLastPaymentData() {
        lastPaymentTokenizedCard = null
        lastPaymentId = null
    }

    override fun onAction(action: PaymentAction) {
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
                        showError(
                            message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay),
                            throwable = null
                        )
                    }
                } else {
                    Logger.e { "GooglePay result is null, but task result is success, this should never happen" }
                    showError(
                        message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay),
                        throwable = null
                    )
                }
            }

            CommonStatusCodes.CANCELED -> {
                Logger.i { "GooglePay process was cancelled by user" }
                retry()
            }

            AutoResolveHelper.RESULT_ERROR -> {
                Logger.e { "GooglePay process failed with error, status message = ${taskResult.status.statusMessage}" }
                showError(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_google_pay),
                    throwable = null
                )
            }
        }
    }

    private fun runPaymentWithGooglePay(token: String) {
        loading()
        val paymentRequest = GooglePayPaymentRequest(
            authParameters = clientAuthParameters,
            paymentDetails = basePaymentParameters(),
            googlePayToken = Base64.encode(token.toByteArray(), Base64.NO_WRAP).toString(Charsets.UTF_8)
        )
        createPaymentUseCase(paymentRequest)
            .catch { error ->
                Logger.e(throwable = error) { "Google pay payment error" }
                showError(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                    throwable = error
                )
            }
            .onEach { result ->
                Logger.d { "Payment result: $result" }
                when (result) {
                    is CreatePaymentResult.Confirmation3DsRequired -> {
                        lastPaymentId = result.paymentId
                        start3ds(url = result.url)
                    }

                    is CreatePaymentResult.Error -> showError(
                        message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                        throwable = result.error
                    )

                    is CreatePaymentResult.Success -> {
                        lastPaymentId = result.paymentId
                        success()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun start3ds(
        url: String,
    ) {
        loading()
        _eventsChannel.trySend(
            PaymentEvent.Start3dsConfirmation(
                url = url
            )
        )
    }

    private fun handlePaymentConfirmationResult(result: ConfirmPaymentResult) {
        Logger.d { "Payment confirmation result $result" }
        when (result) {
            is ConfirmPaymentResult.Completed -> {
                recheckPaymentStatus()
            }

            // payment status should be recheck in case of cancellation
            // because this cancellation can be caused by user action
            // on the moment when payment confirmation request already sent
            is ConfirmPaymentResult.Cancelled -> {
                recheckPaymentStatus()
            }

            is ConfirmPaymentResult.Success -> success()

            is ConfirmPaymentResult.Error -> {
                showError(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                    throwable = result.error
                )
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
                message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                throwable = error
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
            paymentDetails = basePaymentParameters(),
            cardToken = tokenizedCard.token
        )
        createPaymentUseCase(
            params = paymentRequest
        ).catch { error ->
            Logger.e(throwable = error) { "Card token payment error" }
            showError(
                message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                throwable = error
            )
        }.onEach { result: CreatePaymentResult ->
            Logger.d { "Payment result: $result" }
            when (result) {
                is CreatePaymentResult.Confirmation3DsRequired -> {
                    lastPaymentTokenizedCard = tokenizedCard
                    lastPaymentId = result.paymentId
                    start3ds(url = result.url)
                }

                is CreatePaymentResult.Error -> showError(
                    message = resourcesProvider.getString(R.string.rozetka_pay_payment_error_common),
                    throwable = result.error
                )

                is CreatePaymentResult.Success -> {
                    lastPaymentTokenizedCard = tokenizedCard
                    lastPaymentId = result.paymentId
                    success()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun basePaymentParameters() = PaymentDetails(
        amount = parameters.amountParameters.amount,
        currencyCode = parameters.amountParameters.currencyCode,
        externalId = parameters.externalId,
        callbackUrl = parameters.callbackUrl,
        resultUrl = environment.paymentsConfirmation3DsCallbackUrl,
    )

    private fun recheckPaymentStatus() {
        val paymentId = requireNotNull(lastPaymentId)
        checkPaymentStatusUseCase(
            CheckPaymentStatusUseCase.Parameters(
                paymentId = paymentId,
                externalId = parameters.externalId,
                authParameters = clientAuthParameters,
                isBatch = false
            )
        ).catch { error ->
            Logger.e(throwable = error) { "Check payment status error" }
            completedPending()
        }.onEach { paymentData ->
            Logger.d { "Recheck payment data: $paymentData" }
            when (paymentData.status) {
                PaymentStatus.Success -> success()

                PaymentStatus.Failure -> failed(
                    message = paymentData.statusDescription,
                    type = paymentData.statusCode
                )

                PaymentStatus.Init,
                PaymentStatus.Pending,
                    -> completedPending()
            }
        }.launchIn(viewModelScope)
    }

    private fun loading() {
        _uiState.tryEmit(
            _uiState.value.copy(
                displayState = PaymentDisplayState.Loading
            )
        )
    }

    private fun success() {
        val tokenizedCard = if (isTokenizationAllowed()) lastPaymentTokenizedCard else null
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Complete(
                    paymentId = requireNotNull(lastPaymentId),
                    externalId = parameters.externalId,
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
        throwable: Throwable?,
    ) {
        _uiState.tryEmit(
            _uiState.value.copy(
                displayState = PaymentDisplayState.Error(
                    message = message,
                    reason = throwable
                )
            )
        )
    }

    private fun retry() {
        clearLastPaymentData()
        when (parameters.paymentType) {
            is RegularPayment -> {
                _uiState.tryEmit(
                    _uiState.value.copy(
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

    private fun completedPending() {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Pending(
                    externalId = parameters.externalId,
                    paymentId = requireNotNull(lastPaymentId)
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
                    message = if (reason is RozetkaPayException) reason.getReadableMessage() else null,
                    error = reason
                )
            )
        )
    }

    private fun failed(
        message: String?,
        type: String?,
    ) {
        _eventsChannel.trySend(
            PaymentEvent.Result(
                PaymentResult.Failed(
                    paymentId = lastPaymentId,
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
                },
                environment = RozetkaPayKoinContext.koin.get(),
            ) as T
        }
    }
}