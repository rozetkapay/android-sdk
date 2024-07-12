package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RozetkaPayBottomSheet(
    showSheet: MutableState<Boolean> = remember { mutableStateOf(false) },
    modalBottomSheetState: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showSheet.value) {
        ModalBottomSheet(
            containerColor = DomainTheme.colorScheme.surface,
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheetState,
            dragHandle = null,
            windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(
    showBackground = true
)
private fun RozetkaPayBottomSheetPreview() {
    RozetkaPayTheme {
        val showSheet = remember { mutableStateOf(true) }
        RozetkaPayBottomSheet(
            showSheet = showSheet,
            onDismiss = {}
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
