package com.leinaro.move.presentation.boxdetails

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.move.R
import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdInteractor
import com.leinaro.move.domain.usecase.getinventory.GetInventoryInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxInteractor
import com.leinaro.move.presentation.boxdetails.handler.BoxSavedViewHandler
import com.leinaro.move.presentation.boxdetails.handler.ShowInitialDataViewHandler
import com.leinaro.move.presentation.boxdetails.handler.ShowInventoryViewHandler
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory
import com.leinaro.validatable_fields.DefaultValidatableField
import com.leinaro.validatable_fields.ValidationRule
import com.leinaro.validatable_fields.validators.NotEmptyValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxDetailsViewModel @Inject constructor(
  private val notEmptyValidator: NotEmptyValidator,
  private val getBoxByShortIdInteractor: GetBoxByShortIdInteractor,
  private val getImagesByBoxIdInteractor: GetImagesByBoxIdInteractor,
  private val getInventoryInteractor: GetInventoryInteractor,
  private val saveBoxInteractor: SaveBoxInteractor,
  private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<BoxDetailsViewData>() {

  private var boxContent: BoxContent? = null
  private val temporalBitmapList = mutableListOf<Bitmap>()
  // private var inventoryId: Long = savedStateHandle.get<Long>("inventoryId") ?: -1
  //private var inventory: Inventory? = savedStateHandle.get<Inventory>("inventory")

  val locationValidator = DefaultValidatableField(
    ValidationRule(
      validator = notEmptyValidator,
      errorMessage = R.string.required_field_error,
    )
  )

  val descriptionValidator = DefaultValidatableField(
    ValidationRule(
      validator = notEmptyValidator,
      errorMessage = R.string.required_field_error,
    )
  )

  fun onCreate(boxContent: BoxContent?) {
    viewModelScope.launch(Dispatchers.IO) {
      if (boxContent != null) {
        // this@BoxDetailsViewModel.boxContent = boxContent
        setValue(
          BoxDetailsViewData.ShowInitialDataViewData(
            boxContent = boxContent,
            bitmapList = emptyList(),
            temporalBitmapList = emptyList()
          ),
          ShowInitialDataViewHandler
        )
        getImages(boxContent.uuid)
      }

      getInventoryData(savedStateHandle.get<Long>("inventoryId") ?: -1)
    }
  }

  fun onCreate(action: String?, uri: Uri?) {
    viewModelScope.launch(Dispatchers.IO) {
      getBoxData(uri)
    }
  }

  private suspend fun getBoxData(uri: Uri?) {
    uri?.pathSegments?.last()?.let { shortId ->
      getBoxByShortIdInteractor.execute(shortId)
        .onEach {
          this.boxContent = it
          getInventoryData(it?.inventoryId ?: savedStateHandle.get<Long>("inventoryId") ?: -1)
        }
        .collect { boxContent ->
          //  this@BoxDetailsViewModel.boxContent = boxContent
          boxContent?.let {

            setValue(
              BoxDetailsViewData.ShowInitialDataViewData(
                boxContent = boxContent,
                bitmapList = emptyList(),
                temporalBitmapList = emptyList()
              ),
              ShowInitialDataViewHandler
            )
            getImages(boxContent.uuid)
          } ?: run {
            val tempBoxContent = BoxContent(
              uuid = shortId,
              isNew = true,
              inventoryId = savedStateHandle.get<Long>("inventoryId") ?: -1
            )
                this@BoxDetailsViewModel.boxContent = tempBoxContent
            setValue(
              BoxDetailsViewData.ShowInitialDataViewData(
                boxContent = tempBoxContent,
                bitmapList = emptyList(),
                temporalBitmapList = emptyList()
              ),
              ShowInitialDataViewHandler
            )
          }
        }
    }
  }

  private suspend fun getInventoryData(inventoryId: Long) {
    getInventoryInteractor.execute(inventoryId)
      .collect { inventory ->
        setValue(
          BoxDetailsViewData.ShowInventoryViewData(inventory),
          ShowInventoryViewHandler
        )
      }
  }

  private fun validateForm(): Boolean {
    return listOf(
      locationValidator.validate(),
      descriptionValidator.validate(),
    ).contains(false)
  }

  fun save() {
    if (validateForm()) return
    val inventoryId =
      this.boxContent?.inventoryId ?: savedStateHandle.get<Long>("inventoryId") ?: -1
    val boxContent = this.boxContent?.copy(
      location = locationValidator.data.value.orEmpty(),
      description = descriptionValidator.data.value.orEmpty(),
      inventoryId = inventoryId,
    )
    boxContent?.let {
      viewModelScope.launch(Dispatchers.IO) {
        saveBoxInteractor.execute(
          boxContent,
          this@BoxDetailsViewModel.temporalBitmapList.orEmpty()
        )
          .collect {
            setValue(
              BoxDetailsViewData.BoxSaved,
              BoxSavedViewHandler
            )
          }
        temporalBitmapList.clear()

      }
    }
  }

  fun addImages(bitmapList: List<Bitmap>) {
    temporalBitmapList.addAll(bitmapList)
    //viewData.value = viewData.value?.copy(temporalBitmapList = temporalBitmapList)
  }

  private fun getImages(uuid: String) {
    viewModelScope.launch(Dispatchers.IO) {
      getImagesByBoxIdInteractor.execute(uuid)
    }
  }
}

sealed class BoxDetailsViewData {
  data class ShowInitialDataViewData(
    val boxContent: BoxContent,
    val bitmapList: List<Bitmap>,
    val temporalBitmapList: List<Bitmap>
  ) : BoxDetailsViewData()

  data class ShowInventoryViewData(val inventory: Inventory?) : BoxDetailsViewData()
  object BoxSaved : BoxDetailsViewData()
}


