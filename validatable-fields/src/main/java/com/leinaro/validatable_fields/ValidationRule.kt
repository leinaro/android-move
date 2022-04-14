package com.leinaro.validatable_fields

import androidx.annotation.StringRes

data class ValidationRule(
    val validator: StringValidator,
    @StringRes val errorMessage: Int,
) : StringValidator by validator
