package com.rozetkapay.sdk.domain.models

enum class FieldRequirement {
    None,
    Optional,
    Required
}

fun FieldRequirement.required(): Boolean = this == FieldRequirement.Required