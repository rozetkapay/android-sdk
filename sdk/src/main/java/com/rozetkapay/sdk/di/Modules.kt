package com.rozetkapay.sdk.di

import com.rozetkapay.sdk.data.android.AndroidResourcesProvider
import com.rozetkapay.sdk.data.network.ApiTokenizationRepository
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.repository.TokenizationRepository
import com.rozetkapay.sdk.domain.usecases.GetDeviceInfoUseCase
import com.rozetkapay.sdk.domain.usecases.ParseCardDataUseCase
import com.rozetkapay.sdk.domain.usecases.ProvideCardPaymentSystemUseCase
import com.rozetkapay.sdk.domain.usecases.TokenizeCardUseCase
import com.rozetkapay.sdk.domain.validators.CardExpDateValidator
import com.rozetkapay.sdk.domain.validators.CardNumberValidator
import com.rozetkapay.sdk.domain.validators.CvvValidator
import org.koin.dsl.module

internal val useCaseModule = module {
    single<ProvideCardPaymentSystemUseCase> { ProvideCardPaymentSystemUseCase() }
    single<GetDeviceInfoUseCase> { GetDeviceInfoUseCase(get()) }
    single<ParseCardDataUseCase> {
        val resourcesProvider: ResourcesProvider = get()
        ParseCardDataUseCase(
            cardNumberValidator = CardNumberValidator(resourcesProvider),
            cvvValidator = CvvValidator(resourcesProvider),
            expDateValidator = CardExpDateValidator(resourcesProvider),
            resourcesProvider = get()
        )
    }
    single<TokenizeCardUseCase> {
        TokenizeCardUseCase(
            tokenizationRepository = get(),
            getDeviceInfoUseCase = get()
        )
    }
}

internal val repositoryModule = module {
    single<ResourcesProvider> { AndroidResourcesProvider(get()) }
    single<TokenizationRepository> { ApiTokenizationRepository() }
}