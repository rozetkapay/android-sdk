package com.rozetkapay.demo

import android.app.Application
import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.RozetkaPaySdkMode

class RozetkaPayDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RozetkaPaySdk.init(
            appContext = this,
            mode = RozetkaPaySdkMode.Development,
            enableLogging = true
        )
    }
}