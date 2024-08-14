package com.rozetkapay.sdk.di

import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.data.android.AndroidResourcesProvider
import com.rozetkapay.sdk.data.network.ApiPaymentsRepository
import com.rozetkapay.sdk.data.network.ApiProvider
import com.rozetkapay.sdk.data.network.ApiProviderImpl
import com.rozetkapay.sdk.data.network.ApiTokenizationRepository
import com.rozetkapay.sdk.data.network.RequestSigner
import com.rozetkapay.sdk.data.network.RequestSignerImpl
import com.rozetkapay.sdk.data.network.ResponseDecryptor
import com.rozetkapay.sdk.data.network.ResponseDecryptorImpl
import com.rozetkapay.sdk.data.network.createHttpClient
import com.rozetkapay.sdk.domain.repository.PaymentsRepository
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
import com.rozetkapay.sdk.domain.usecases.CreatePaymentUseCase
import com.rozetkapay.sdk.domain.usecases.GetDeviceInfoUseCase
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.domain.validators.CardExpDateValidator
import com.rozetkapay.sdk.domain.validators.CardNumberValidator
import com.rozetkapay.sdk.domain.validators.CardholderNameValidator
import com.rozetkapay.sdk.domain.validators.CvvValidator
import com.rozetkapay.sdk.init.RozetkaPaySdkMode
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import org.koin.dsl.module

internal val useCaseModule = module {
    single<ProvideCardPaymentSystemUseCase> { ProvideCardPaymentSystemUseCase() }
    single<GetDeviceInfoUseCase> { GetDeviceInfoUseCase(get()) }
    factory<ParseCardDataUseCase> {
        val resourcesProvider: ResourcesProvider = get()
        ParseCardDataUseCase(
            cardNumberValidator = CardNumberValidator(resourcesProvider),
            cvvValidator = CvvValidator(resourcesProvider),
            expDateValidator = CardExpDateValidator(
                resourcesProvider = resourcesProvider,
                expirationValidationRule = RozetkaPaySdk.validationRules.cardExpirationDateValidationRule
            ),
            cardholderNameValidator = CardholderNameValidator(resourcesProvider),
            resourcesProvider = resourcesProvider,
        )
    }
    single<TokenizeCardUseCase> {
        TokenizeCardUseCase(
            tokenizationRepository = get(),
            getDeviceInfoUseCase = get(),
            provideCardPaymentSystemUseCase = get()
        )
    }
    single<CreatePaymentUseCase> {
        CreatePaymentUseCase(
            paymentsRepository = get()
        )
    }
}

internal val repositoryModule = module {
    single<ResourcesProvider> { AndroidResourcesProvider(get()) }
    single<TokenizationRepository> {
        ApiTokenizationRepository(
            apiProvider = get(),
            httpClient = get(),
            requestSigner = get(),
            responseDecryptor = get()
        )
    }
    single<PaymentsRepository> {
        ApiPaymentsRepository(
            apiProvider = get(),
            httpClient = get()
        )
    }
}

internal val networkModule = module {
    single<HttpClient> {
        createHttpClient(
            logLevel = if (RozetkaPaySdk.mode == RozetkaPaySdkMode.Development) {
                LogLevel.ALL
            } else {
                // TODO: log level ALL temporarily enabled for development purposes
                // should be replaced with HEADERS before release
                LogLevel.ALL
                // LogLevel.HEADERS
            }
        )
    }
    single<ApiProvider> { ApiProviderImpl() }
    single<RequestSigner> { RequestSignerImpl() }
    single<ResponseDecryptor> { ResponseDecryptorImpl() }
}