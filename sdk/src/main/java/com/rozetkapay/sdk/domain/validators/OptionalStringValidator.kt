package com.rozetkapay.sdk.domain.validators

internal class OptionalStringValidator(
    val validator: Validator<String?>,
) : Validator<String?>() {

    override fun validate(value: String?): ValidationResult {
        return if (value.isNullOrBlank()) {
            ValidationResult.Valid
        } else {
            validator.validate(value)
        }
    }
}