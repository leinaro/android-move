package com.leinaro.move.presentation.inventorybanner

import androidx.lifecycle.viewModelScope
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.move.R
import com.leinaro.move.domain.usecase.saveinventory.SaveInventoryInteractor
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.presentation.inventorybanner.handler.SavedInventoryViewHandler
import com.leinaro.validatable_fields.DefaultValidatableField
import com.leinaro.validatable_fields.ValidationRule
import com.leinaro.validatable_fields.validators.NotEmptyValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewInventoryViewModel @Inject constructor(
  private val notEmptyValidator: NotEmptyValidator,
  private val saveInventoryInteractor: SaveInventoryInteractor,
) : BaseViewModel<NewInventoryViewData>() {

  val originValidator = DefaultValidatableField(
    ValidationRule(
      validator = notEmptyValidator,
      errorMessage = R.string.required_field_error,
    )
  )
  val arrivalValidator = DefaultValidatableField(
    ValidationRule(
      validator = notEmptyValidator,
      errorMessage = R.string.required_field_error,
    )
  )

  private fun validateForm(): Boolean {
    return listOf(
      originValidator.validate(),
      arrivalValidator.validate(),
    ).contains(false)
  }

  fun save() {
    if (validateForm()) return
    val inventory = Inventory(
      origin = originValidator.data.value.orEmpty(),
      destination = arrivalValidator.data.value.orEmpty(),
    )
    viewModelScope.launch(Dispatchers.IO) {
      saveInventoryInteractor.execute(inventory)
        .collect { inventory ->
          setValue(NewInventoryViewData.SavedInventory(inventory), SavedInventoryViewHandler)
        }
      //viewData.value = viewData.value?.copy(temporalBitmapList = temporalBitmapList)
    }
  }
}

sealed class NewInventoryViewData {
  data class SavedInventory(val inventory: Inventory) : NewInventoryViewData()
}

