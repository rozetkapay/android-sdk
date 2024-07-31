package com.rozetkapay.sdk.presentation.util

import com.rozetkapay.sdk.domain.models.FieldRequirement

internal fun FieldRequirement.isShow(): Boolean {
    return this != FieldRequirement.None
}