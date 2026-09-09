package com.rozetkapay.sdk.data.network.converters

import com.rozetkapay.sdk.data.network.models.LocalizedStatusDescription
import com.rozetkapay.sdk.init.RozetkaPayLanguage

internal fun LocalizedStatusDescription.resolveDescription(language: RozetkaPayLanguage): String? {
    val localized = when (language) {
        RozetkaPayLanguage.English -> statusDescriptionEn
        RozetkaPayLanguage.Ukrainian, RozetkaPayLanguage.System -> statusDescriptionUk
    }
    return localized ?: statusDescription
}