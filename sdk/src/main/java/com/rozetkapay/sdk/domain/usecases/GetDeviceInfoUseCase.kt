package com.rozetkapay.sdk.domain.usecases

import android.content.Context
import android.provider.Settings
import com.rozetkapay.sdk.BuildConfig
import com.rozetkapay.sdk.domain.models.DeviceInfo

internal class GetDeviceInfoUseCase(
    private val context: Context,
) {

    operator fun invoke(): DeviceInfo {
        return DeviceInfo(
            platform = "Android",
            sdkVersion = BuildConfig.VERSION,
            osVersion = android.os.Build.VERSION.SDK_INT.toString(),
            osBuildVersion = android.os.Build.VERSION.RELEASE,
            osBuildNumber = "no data",
            deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        )
    }
}