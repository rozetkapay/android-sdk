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
    internal var isDevMode: Boolean = false
    internal var isInitialized: Boolean = false

    fun init(
        appContext: Context,
        isDevMode: Boolean = false,
    ) {
        this._appContext = appContext
        this.isDevMode = isDevMode
        this.isInitialized = true
    }
}

internal fun checkInitialization() {
    if (!RozetkaPaySdk.isInitialized) {
        throw IllegalStateException("RozetkaPaySdk is not initialized")
    }
}