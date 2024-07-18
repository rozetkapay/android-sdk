package com.rozetkapay.sdk.domain.models

data class DeviceInfo(
    val platform: String,
    val sdkVersion: String,
    val osVersion: String,
    val osBuildVersion: String,
    val osBuildNumber: String,
    val deviceId: String,
)