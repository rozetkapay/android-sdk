package com.rozetkapay.sdk.domain.validators

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.repository.ResourcesProvider

internal class CvvValidator(
    private val resourcesProvider: ResourcesProvider,
) : Validator<String?>() {
    override fun validate(value: String?): ValidationResult {
        val isValid = !value.isNullOrBlank()
            && value.matches("^[0-9]+$".toRegex())
            && value.length == 3
        return if (isValid) ValidationResult.Valid else generateError()
    }

    private fun generateError(): ValidationResult.Error {
        return ValidationResult.Error(
            resourcesProvider.getString(R.string.rozetka_pay_form_validation_cvv_incorrect)
        )
    }
}

