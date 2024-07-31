package com.rozetkapay.sdk.domain.models.tokenization

import android.os.Parcelable
import com.rozetkapay.sdk.domain.models.FieldRequirement
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenizationParameters(
    val cardNameField: FieldRequirement = FieldRequirement.None,
    val emailField: FieldRequirement = FieldRequirement.None,
    val cardholderNameField: FieldRequirement = FieldRequirement.None,
) : Parcelable
