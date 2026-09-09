package com.rozetkapay.sdk.util

import com.rozetkapay.sdk.init.RozetkaPayLanguage
import java.util.Locale

internal fun RozetkaPayLanguage.resolveEffective(locale: Locale): RozetkaPayLanguage = when (this) {
    RozetkaPayLanguage.System -> {
        if (locale.language == "en") RozetkaPayLanguage.English else RozetkaPayLanguage.Ukrainian
    }
    RozetkaPayLanguage.Ukrainian, RozetkaPayLanguage.English -> this
}
