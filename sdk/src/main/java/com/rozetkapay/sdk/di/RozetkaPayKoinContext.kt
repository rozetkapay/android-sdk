package com.rozetkapay.sdk.di

import com.rozetkapay.sdk.RozetkaPaySdk
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication

internal object RozetkaPayKoinContext {
    private val koinApp = koinApplication {
        androidContext(RozetkaPaySdk.appContext)
        modules(
            commonModule,
            useCaseModule,
            repositoryModule,
            networkModule
        )
    }

    val koin = koinApp.koin
}