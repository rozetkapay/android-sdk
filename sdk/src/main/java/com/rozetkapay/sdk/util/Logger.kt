package com.rozetkapay.sdk.util

import android.util.Log
import com.rozetkapay.sdk.RozetkaPaySdk

@Suppress("unused")
internal object Logger {

    private val isLogEnabled: Boolean
        get() = RozetkaPaySdk.isLoggingEnabled

    const val DEFAULT_TAG = "RozetkaPaySdk"

    inline fun v(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.v(tag, message(), throwable)
    }

    inline fun d(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.d(tag, message(), throwable)
    }

    inline fun i(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.i(tag, message(), throwable)
    }

    inline fun w(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.w(tag, message(), throwable)
    }

    inline fun e(tag: String = DEFAULT_TAG, throwable: Throwable? = null, message: () -> String) {
        if (isLogEnabled) Log.e(tag, message(), throwable)
    }
}