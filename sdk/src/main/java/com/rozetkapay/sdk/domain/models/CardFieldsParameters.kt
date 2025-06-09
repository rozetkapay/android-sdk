package com.rozetkapay.sdk.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardFieldsParameters(
    val cardNameField: FieldRequirement = FieldRequirement.None,
    val emailField: FieldRequirement = FieldRequirement.None,
    val cardholderNameField: FieldRequirement = FieldRequirement.None,
) : Parcelable