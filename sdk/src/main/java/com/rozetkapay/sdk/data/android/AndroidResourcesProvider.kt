package com.rozetkapay.sdk.data.android

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import java.util.Locale

internal class AndroidResourcesProvider(
    private val context: Context,
) : ResourcesProvider {
    override fun getString(stringResId: Int, vararg formatArgs: Any): String {
        return context.getString(stringResId, *formatArgs)
    }

    override fun getCurrentLocale(): Locale {
        return AppCompatDelegate.getApplicationLocales()[0]
            ?: ConfigurationCompat.getLocales(context.resources.configuration)[0]
            ?: Locale.getDefault()
    }
}