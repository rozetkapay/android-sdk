package com.rozetkapay.sdk

import android.content.Context
import com.rozetkapay.sdk.init.RozetkaPaySdkMode
import com.rozetkapay.sdk.init.RozetkaPaySdkValidationRules

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
    internal var validationRules: RozetkaPaySdkValidationRules = RozetkaPaySdkValidationRules()

    fun init(
        appContext: Context,
        mode: RozetkaPaySdkMode = RozetkaPaySdkMode.Production,
        enableLogging: Boolean = false,
        validationRules: RozetkaPaySdkValidationRules = RozetkaPaySdkValidationRules()
    ) {
        this._appContext = appContext
        this.mode = mode
        this.isInitialized = true
        this.isLoggingEnabled = enableLogging
        this.validationRules = validationRules
    }
}

