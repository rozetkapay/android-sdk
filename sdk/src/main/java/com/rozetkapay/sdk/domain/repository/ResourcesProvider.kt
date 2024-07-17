package com.rozetkapay.sdk.domain.repository

import androidx.annotation.StringRes

internal interface ResourcesProvider {
    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String
}