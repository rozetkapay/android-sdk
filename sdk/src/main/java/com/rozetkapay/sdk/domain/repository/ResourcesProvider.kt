package com.rozetkapay.sdk.domain.repository

import androidx.annotation.StringRes
import java.util.Locale

internal interface ResourcesProvider {
    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String
    fun getCurrentLocale(): Locale
}