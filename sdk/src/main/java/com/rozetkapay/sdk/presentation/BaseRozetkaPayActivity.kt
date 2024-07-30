package com.rozetkapay.sdk.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.rozetkapay.sdk.init.checkInitialization
import com.rozetkapay.sdk.presentation.util.RozetkaPayAnimations

internal abstract class BaseRozetkaPayActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        checkInitialization()
        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        fadeOut()
    }

    private fun fadeOut() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE, RozetkaPayAnimations.fadeIn, 0
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(RozetkaPayAnimations.fadeIn, 0)
        }
    }
}
