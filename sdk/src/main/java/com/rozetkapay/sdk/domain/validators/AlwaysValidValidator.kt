package com.rozetkapay.sdk.domain.validators

object AlwaysValidValidator : Validator<String?>() {
    override fun validate(value: String?): ValidationResult {
        return ValidationResult.Valid
    }
}