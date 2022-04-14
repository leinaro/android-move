package com.leinaro.validatable_fields

import androidx.lifecycle.MutableLiveData

open class DefaultAutoCompleteValidatableField<T>(
  private vararg val rules: ValidationRule,
) : AutoCompleteValidatableField<T> {
  override val selectedItem = MutableLiveData<T>()
  override val text = MutableLiveData<String>()
  override val errorMessage = MutableLiveData<Int?>()

  override fun validate(): Boolean {
    for (rule in rules) {
      if (!rule.validate(text.value)) {
        errorMessage.postValue(rule.errorMessage)
        return false
      }
    }
    errorMessage.postValue(null)
    return true
  }
}

open class DefaultValidatableField(
  private vararg val rules: ValidationRule,
) : ValidatableField {

  override val data = MutableLiveData<String>()
  override val errorMessage = MutableLiveData<Int?>()
  override var isOptional: Boolean = false

  override fun validate(): Boolean {
    if (isOptional && data.value.isNullOrEmpty()) {
      errorMessage.postValue(null)
      return true
    }
    for (rule in rules) {
      if (!rule.validate(data.value)) {
        errorMessage.postValue(rule.errorMessage)
        return false
      }
    }
    errorMessage.postValue(null)
    return true
  }
}