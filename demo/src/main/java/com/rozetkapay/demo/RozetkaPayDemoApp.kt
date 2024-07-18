package com.rozetkapay.demo

import android.app.Application
import com.rozetkapay.sdk.RozetkaPaySdk

class RozetkaPayDemoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RozetkaPaySdk.init(
            appContext = this,
        )
    }
}