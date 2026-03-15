package com.rozetkapay.sdk.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.rozetkapay.sdk.di.RozetkaPayKoinContext
import org.koin.compose.KoinContext

@Suppress("DEPRECATION")
@Composable
internal fun RozetkaPayContext(
    content: @Composable () -> Unit,
) {
    val isPreview: Boolean = LocalInspectionMode.current
    if (isPreview) {
        content()
    } else {
        KoinContext(context = RozetkaPayKoinContext.koin) {
            content()
        }
    }
}