package com.leinaro.move.presentation.boxdetails

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.R
import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxInteractor
import com.leinaro.validatable_fields.DefaultValidatableField
import com.leinaro.validatable_fields.ValidationRule
import com.leinaro.validatable_fields.validators.NotEmptyValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxDetailsViewModel @Inject constructor(
  private val notEmptyValidator: NotEmptyValidator,
  private val getBoxByShortIdInteractor: GetBoxByShortIdInteractor,
  private val getImagesByBoxIdInteractor: GetImagesByBoxIdInteractor,
  private val saveBoxInteractor: SaveBoxInteractor,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  val viewData = MutableStateFlow<BoxDetailsViewData?>(null)

  private val temporalBitmapList = mutableListOf<Bitmap>()

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
    if (boxContent != null) {
      viewData.value = BoxDetailsViewData(
        boxContent = boxContent,
        bitmapList = emptyList(),
        temporalBitmapList = emptyList()
      )
      getImages(boxContent.uuid)
    }
  }

  fun onCreate(action: String?, uri: Uri?) {
    viewModelScope.launch(Dispatchers.IO) {
      uri?.pathSegments?.last()?.let { shortId ->
        getBoxByShortIdInteractor.execute(shortId)
          .collect { boxContent ->
            boxContent?.let {
              viewData.value = BoxDetailsViewData(
                boxContent = boxContent,
                bitmapList = emptyList(),
                temporalBitmapList = emptyList()
              )
              getImages(boxContent.uuid)
            } ?: run {
              val tempBoxContent = BoxContent(
                uuid = shortId,
                isNew = true,
              )
              viewData.value = BoxDetailsViewData(
                boxContent = tempBoxContent,
                bitmapList = emptyList(),
                temporalBitmapList = emptyList(),
              )
            }
          }
      }
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
    val boxContent = viewData.value?.boxContent?.copy(
      location = locationValidator.data.value.orEmpty(),
      description = descriptionValidator.data.value.orEmpty(),
    )
    boxContent?.let {
      viewModelScope.launch(Dispatchers.IO) {
        saveBoxInteractor.execute(
          boxContent,
          viewData.value?.temporalBitmapList.orEmpty()
        )
        temporalBitmapList.clear()
      }
    }
  }

  fun addImages(bitmapList: List<Bitmap>) {
    temporalBitmapList.addAll(bitmapList)
    viewData.value = viewData.value?.copy(temporalBitmapList = temporalBitmapList)
  }

  private fun getImages(uuid: String) {
    viewModelScope.launch(Dispatchers.IO) {
      getImagesByBoxIdInteractor.execute(uuid)
    }
  }
}

data class BoxDetailsViewData(
  val boxContent: BoxContent,
  val bitmapList: List<Bitmap>,
  val temporalBitmapList: List<Bitmap>
)

