package com.rozetkapay.sdk

import android.content.Context

object RozetkaPaySdk {
    private lateinit var _appContext: Context
    internal val appContext: Context
        get() {
            if (!isInitialized) {
                throw IllegalStateException("RozetkaPaySdk is not initialized")
            }
            return _appContext
        }
    internal var mode: RozetkaPaySdkMode = RozetkaPaySdkMode.Production
    internal var isInitialized: Boolean = false
    internal var isLoggingEnabled: Boolean = false

    fun init(
        appContext: Context,
        mode: RozetkaPaySdkMode = RozetkaPaySdkMode.Production,
        enableLogging: Boolean = false,
    ) {
        this._appContext = appContext
        this.mode = mode
        this.isInitialized = true
        this.isLoggingEnabled = enableLogging
    }
}

internal fun checkInitialization() {
    if (!RozetkaPaySdk.isInitialized) {
        throw IllegalStateException("RozetkaPaySdk is not initialized")
    }
}

enum class RozetkaPaySdkMode {
    Production,
    Development
}