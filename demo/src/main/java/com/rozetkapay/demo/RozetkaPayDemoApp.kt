package com.rozetkapay.demo

import android.app.Application
import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.init.RozetkaPaySdkMode
import com.rozetkapay.sdk.init.RozetkaPaySdkValidationRules

class RozetkaPayDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RozetkaPaySdk.init(
            appContext = this,
            mode = RozetkaPaySdkMode.Development,
            enableLogging = true,
            validationRules = RozetkaPaySdkValidationRules()
        )
    }
}