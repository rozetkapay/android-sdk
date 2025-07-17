package com.rozetkapay.sdk.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.rozetkapay.sdk.init.checkInitialization
import com.rozetkapay.sdk.presentation.theme.ThemeMode
import com.rozetkapay.sdk.presentation.util.RozetkaPayAnimations

internal abstract class BaseRozetkaPayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        checkInitialization()
        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        fadeOut()
    }

    protected fun setupNightMode(mode: ThemeMode?) {
        when (mode) {
            ThemeMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }.let {
            AppCompatDelegate.setDefaultNightMode(it)
        }
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
