package com.rozetkapay.sdk.init

import com.rozetkapay.sdk.RozetkaPaySdk

internal fun checkInitialization() {
    if (!RozetkaPaySdk.isInitialized) {
        throw IllegalStateException("RozetkaPaySdk is not initialized")
    }
}