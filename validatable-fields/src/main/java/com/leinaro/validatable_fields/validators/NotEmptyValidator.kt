package com.leinaro.validatable_fields.validators

import com.leinaro.validatable_fields.StringValidator

interface NotEmptyValidator : StringValidator

object AppNotEmptyValidator : NotEmptyValidator {
    override fun validate(input: String?): Boolean = input?.isNotBlank() ?: false
}