package com.rozetkapay.sdk.data.android

import android.content.Context
import com.rozetkapay.sdk.domain.repository.ResourcesProvider

internal class AndroidResourcesProvider(
    private val context: Context,
) : ResourcesProvider {
    override fun getString(stringResId: Int, vararg formatArgs: Any): String {
        return context.getString(stringResId, *formatArgs)
    }
}