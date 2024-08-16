package com.rozetkapay.sdk.presentation.tokenization

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.tokenization.launcher.DefaultTokenizationSheetLauncher
import com.rozetkapay.sdk.presentation.tokenization.launcher.TokenizationSheetLauncher

@Composable
fun rememberTokenizationSheet(
    onResultCallback: TokenizationSheetResultCallback,
): TokenizationSheet {
    val resultCallback by rememberUpdatedState(newValue = onResultCallback::onTokenizationSheetResult)
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = TokenizationSheetContract(),
        onResult = resultCallback,
    )
    val context = LocalContext.current
    return remember(onResultCallback) {
        val launcher = DefaultTokenizationSheetLauncher(
            activityResultLauncher = activityResultLauncher,
            application = context.applicationContext as Application,
            callback = onResultCallback,
        )
        TokenizationSheet(
            launcher = launcher
        )
    }
}

@Suppress("unused")
class TokenizationSheet internal constructor(
    private val launcher: TokenizationSheetLauncher,
) {
    constructor(
        activity: ComponentActivity,
        callback: TokenizationSheetResultCallback,
    ) : this(
        DefaultTokenizationSheetLauncher(activity, callback)
    )

    constructor(
        fragment: Fragment,
        callback: TokenizationSheetResultCallback,
    ) : this(
        DefaultTokenizationSheetLauncher(fragment, callback)
    )

    fun show(
        client: ClientWidgetParameters,
        parameters: TokenizationParameters = TokenizationParameters(),
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    ) {
        launcher.present(
            client = client,
            parameters = parameters,
            themeConfigurator = themeConfigurator,
        )
    }
}
