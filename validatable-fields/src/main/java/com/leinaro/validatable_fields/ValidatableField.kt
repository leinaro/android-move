package com.leinaro.validatable_fields

import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout

interface ValidatableField {
  fun validate(): Boolean
  var isOptional: Boolean
  val data: MutableLiveData<String>
  val errorMessage: LiveData<Int?> // String Resource ID
}

fun ValidatableField.bindTextInputLayout(
  viewLifecycleOwner: LifecycleOwner,
  textInputLayout: TextInputLayout,
  isOptional: Boolean = false,
) = with(textInputLayout) {

  this@bindTextInputLayout.isOptional = isOptional
  if (isOptional) {
    hint = hint.toString() + " (Opcional)"
  }

  data.observe(viewLifecycleOwner) {
    if (it != editText?.text.toString()) {
      //        editText?.setDistinctText(newText)
      editText?.setText(it)
    }
  }

  errorMessage.observe(viewLifecycleOwner) {
    error = it?.let { context.getString(it) }
  }

  editText?.doAfterTextChanged {
    error = null
    data.value = it?.toString().orEmpty()
    isErrorEnabled = false
  }
  editText?.setOnFocusChangeListener { _, hasFocus ->
    if (!hasFocus) { // On focus lost
      validate()
    }
  }
}