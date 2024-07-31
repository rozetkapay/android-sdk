package com.rozetkapay.sdk.domain.validators

import android.util.Patterns
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.repository.ResourcesProvider

internal class EmailValidator(
    private val resourcesProvider: ResourcesProvider,
) : Validator<String?>() {

    private val pattern = Patterns.EMAIL_ADDRESS

    override fun validate(value: String?): ValidationResult {
        return if (value == null || !pattern.matcher(value).matches()) {
            ValidationResult.Error(resourcesProvider.getString(R.string.rozetka_pay_form_validation_email_incorrect))
        } else {
            ValidationResult.Valid
        }
    }
}