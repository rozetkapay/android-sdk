package com.rozetkapay.sdk.presentation.tokenization.launcher

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.tokenization.TokenizationSheetContract
import com.rozetkapay.sdk.presentation.tokenization.TokenizationSheetResultCallback
import com.rozetkapay.sdk.presentation.util.RozetkaPayAnimations

internal class DefaultTokenizationSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<TokenizationSheetContract.Parameters>,
    private val application: Application,
    private val callback: TokenizationSheetResultCallback,
) : TokenizationSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: TokenizationSheetResultCallback,
    ) : this(
        activityResultLauncher = activity.registerForActivityResult(TokenizationSheetContract()) {
            callback.onTokenizationSheetResult(it)
        },
        application = activity.application,
        callback = callback,
    )

    constructor(
        fragment: Fragment,
        callback: TokenizationSheetResultCallback,
    ) : this(
        activityResultLauncher = fragment.registerForActivityResult(TokenizationSheetContract()) {
            callback.onTokenizationSheetResult(it)
        },
        application = fragment.requireActivity().application,
        callback = callback,
    )

    override fun present(
        client: ClientParameters,
        themeConfigurator: RozetkaPayThemeConfigurator,
    ) {
        val parameters = TokenizationSheetContract.Parameters(
            client = client,
            themeConfigurator = themeConfigurator,
        )
        val options = ActivityOptionsCompat.makeCustomAnimation(
            application.applicationContext,
            RozetkaPayAnimations.fadeIn,
            RozetkaPayAnimations.fadeOut,
        )
        try {
            activityResultLauncher.launch(parameters, options)
        } catch (e: IllegalStateException) {
            val error = IllegalStateException("The host activity is not in a valid state", e)
            callback.onTokenizationSheetResult(TokenizationResult.Failed(error))
        }
    }
}
