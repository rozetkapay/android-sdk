package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.domain.RozetkaPayConfig
import com.rozetkapay.sdk.domain.models.RozetkaPayEnvironment
import com.rozetkapay.sdk.init.RozetkaPaySdkMode

internal interface EnvironmentProvider {
    val environment: RozetkaPayEnvironment
}

internal object EnvironmentProviderImpl : EnvironmentProvider {
    override val environment: RozetkaPayEnvironment
        get() = when (RozetkaPaySdk.mode) {
            RozetkaPaySdkMode.Production -> RozetkaPayConfig.prodEnvironment
            RozetkaPaySdkMode.Development -> RozetkaPayConfig.devEnvironment
        }
}