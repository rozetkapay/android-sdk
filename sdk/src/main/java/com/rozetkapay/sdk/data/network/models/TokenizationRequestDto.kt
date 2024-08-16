package com.rozetkapay.sdk.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenizationRequestDto(
    @SerialName("card_number")
    val cardNumber: String,
    @SerialName("card_exp_month")
    val cardExpMonth: Int,
    @SerialName("card_exp_year")
    val cardExpYear: Int,
    @SerialName("card_cvv")
    val cardCvv: String,
    @SerialName("card_holder_name")
    val cardholderName: String? = null,
    @SerialName("platform")
    val platform: String,
    @SerialName("sdk_version")
    val sdkVersion: String,
    @SerialName("os_version")
    val osVersion: String,
    @SerialName("os_build_version")
    val osBuildVersion: String,
    @SerialName("os_build_number")
    val osBuildNumber: String,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("device_ip")
    val deviceIp: String? = null,
    @SerialName("device_manufacturer")
    val deviceManufacturer: String? = null,
    @SerialName("device_brand")
    val deviceBrand: String? = null,
    @SerialName("device_model")
    val deviceModel: String? = null,
    @SerialName("device_tags")
    val deviceTags: String? = null,
    @SerialName("device_screenRes")
    val deviceScreenRes: String? = null,
    @SerialName("device_locale")
    val deviceLocale: String? = null,
    @SerialName("device_time_zone")
    val deviceTimeZone: String? = null,
    @SerialName("app_name")
    val appName: String? = null,
    @SerialName("app_package")
    val appPackage: String? = null,
    @SerialName("customer_id")
    val customerId: String? = null,
    @SerialName("customer_email")
    val customerEmail: String? = null,
    @SerialName("device_country")
    val deviceCountry: String? = null,
    @SerialName("device_city")
    val deviceCity: String? = null,
)