package com.leinaro.validatable_fields

import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout

interface AutoCompleteValidatableField<T> {
    fun validate(): Boolean
    val selectedItem: MutableLiveData<T>
    val text: MutableLiveData<String>
    val errorMessage: LiveData<Int?> // String Resource ID
}

fun <T> AutoCompleteValidatableField<T>.bindTextInputLayout(
    viewLifecycleOwner: LifecycleOwner,
    textInputLayout: TextInputLayout,
) = with(textInputLayout) {
    text.observe(viewLifecycleOwner) {
        if (it != editText?.text.toString()) {
            //        editText?.setDistinctText(newText)
            editText?.setText(it)
        }
    }

    errorMessage.observe(viewLifecycleOwner) {
        error = it?.let { context.getString(it) }
    }

    if (editText !is AutoCompleteTextView) {
        throw Exception("No hay un autocomplete")
    }

    editText?.doAfterTextChanged {
        error = null
        text.value = it?.toString().orEmpty()
        isErrorEnabled = false
    }

    val autoCompleteTextView = (editText as AutoCompleteTextView)
    autoCompleteTextView
    autoCompleteTextView.setOnItemClickListener { adapterView, view, position, id ->
        selectedItem.value = autoCompleteTextView.adapter.getItem(position) as T
    }
    editText?.setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) { // On focus lost
            validate()
        }
    }
}