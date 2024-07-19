package com.rozetkapay.sdk.util

import android.util.Log
import com.rozetkapay.sdk.RozetkaPaySdk

internal object Logger {

    private val isLogEnabled: Boolean
        get() = RozetkaPaySdk.isLoggingEnabled

    private const val DEFAULT_TAG = "RozetkaPaySdk"

    fun v(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.v(tag, message(), throwable)
    }

    fun d(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.d(tag, message(), throwable)
    }

    fun i(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.i(tag, message(), throwable)
    }

    fun w(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.w(tag, message(), throwable)
    }

    fun e(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.e(tag, message(), throwable)
    }
}