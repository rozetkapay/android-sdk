package com.rozetkapay.demo.presentation.util

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleErrorsFlow(
    errorsFlow: Flow<String>,
    snackbarHostState: SnackbarHostState,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            errorsFlow.collect { message ->
                Log.d("SNACKBAR", "Showing snackbar with message: $message")
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}
