package com.rozetkapay.sdk.domain.validators

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.repository.ResourcesProvider

internal class RequiredFieldValidator(
    private val resourcesProvider: ResourcesProvider,
) : Validator<String?>() {
    override fun validate(value: String?): ValidationResult {
        return if (value.isNullOrBlank()) {
            ValidationResult.Error(
                resourcesProvider.getString(R.string.rozetka_pay_form_validation_field_empty)
            )
        } else {
            ValidationResult.Valid
        }
    }
}