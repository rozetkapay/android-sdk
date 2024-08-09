package com.rozetkapay.sdk

import android.content.Context
import android.util.Log
import com.rozetkapay.sdk.init.RozetkaPaySdkMode
import com.rozetkapay.sdk.init.RozetkaPaySdkValidationRules
import com.rozetkapay.sdk.util.Logger

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
        validationRules: RozetkaPaySdkValidationRules = RozetkaPaySdkValidationRules(),
    ) {
        this._appContext = appContext
        this.mode = mode
        this.isInitialized = true
        this.isLoggingEnabled = enableLogging
        this.validationRules = validationRules
        checkParameters()
    }

    private fun checkParameters() {
        // warning if logging is enabled
        if (this.isLoggingEnabled) {
            Log.w(
                Logger.DEFAULT_TAG,
                "⚠️ WARNING: LOGGING IS ENABLED!\nTHIS SHOULD ONLY BE USED IN A DEVELOPMENT ENVIRONMENT. ⚠️"
            )
        }
        // warning if not in production mode
        if (mode != RozetkaPaySdkMode.Production) {
            Log.w(
                Logger.DEFAULT_TAG,
                "⚠️ WARNING: SDK IS RUNNING IN ${mode.name.uppercase()} MODE!\n" +
                    "THIS CONFIGURATION SHOULD NOT BE USED IN PRODUCTION. ⚠️"
            )
        }
    }
}

