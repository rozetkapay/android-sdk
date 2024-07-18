package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.RozetkaPaySdk
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RozetkaPayBottomSheet(
    showSheet: MutableState<Boolean> = remember { mutableStateOf(false) },
    modalBottomSheetState: SheetState = rememberRozetkaPayBottomSheetState(),
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showSheet.value) {
        ModalBottomSheet(
            modifier = Modifier.padding(top = 24.dp),
            containerColor = DomainTheme.colors.surface,
            contentColor = DomainTheme.colors.onSurface,
            shape = RoundedCornerShape(
                topStart = DomainTheme.sizes.sheetCornerRadius,
                topEnd = DomainTheme.sizes.sheetCornerRadius
            ),
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheetState,
            dragHandle = null,
            properties = ModalBottomSheetProperties(
                shouldDismissOnBackPress = false,
            ),
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        ) {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun SheetCloseHeader(
    onClose: () -> Unit,
    isDevMode: Boolean = RozetkaPaySdk.isDevMode,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton(
            onClick = onClose
        )
        if (isDevMode) {
            Spacer(modifier = Modifier.weight(1f))
            DevBadge()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberRozetkaPayBottomSheetState() = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = { it != SheetValue.Hidden }
)

private const val SHEET_PADDING_TOP_DP = 20
private const val SHEET_PADDING_BOTTOM_DP = 42
private const val SHEET_PADDING_HORIZONTAL_DP = 16

@Composable
internal fun Modifier.Companion.inSheetPaddings() = this
    .padding(
        top = SHEET_PADDING_TOP_DP.dp,
        start = SHEET_PADDING_HORIZONTAL_DP.dp,
        end = SHEET_PADDING_HORIZONTAL_DP.dp,
        bottom = SHEET_PADDING_BOTTOM_DP.dp,
    )

@Composable
internal fun Modifier.inSheetPaddings() = this
    .padding(
        top = SHEET_PADDING_TOP_DP.dp,
        start = SHEET_PADDING_HORIZONTAL_DP.dp,
        end = SHEET_PADDING_HORIZONTAL_DP.dp,
        bottom = SHEET_PADDING_BOTTOM_DP.dp,
    )

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
                    .height(600.dp)
            )
        }
    }
}
