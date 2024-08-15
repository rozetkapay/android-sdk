package com.rozetkapay.demo.presentation.util

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleErrorsFlow(
    errorsFlow: Flow<String>,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var dialogState by remember { mutableStateOf<ErrorDialogState>(ErrorDialogState.Closed) }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            errorsFlow.collect { message ->
                dialogState = ErrorDialogState.Error(message)
            }
        }
    }
    if (dialogState is ErrorDialogState.Error) {
        AlertDialog(
            onDismissRequest = {
                dialogState = ErrorDialogState.Closed
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = "error-icon",
                )
            },
            title = { Text(text = "Error") },
            text = { Text(text = (dialogState as? ErrorDialogState.Error)?.message ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogState = ErrorDialogState.Closed
                    }
                ) {
                    Text("OK")
                }
            },
        )
    }
}

sealed interface ErrorDialogState {
    data class Error(val message: String) : ErrorDialogState
    data object Closed : ErrorDialogState
}