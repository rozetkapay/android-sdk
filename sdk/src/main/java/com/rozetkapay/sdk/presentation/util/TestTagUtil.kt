package com.rozetkapay.sdk.presentation.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId

fun Modifier.withResourceId(id: String?): Modifier = id?.let { id ->
    Modifier
        .semantics {
            testTag = id
            testTagsAsResourceId = true
        }
        .then(this)
} ?: this