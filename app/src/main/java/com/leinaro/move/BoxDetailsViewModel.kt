package com.leinaro.move

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveImagesInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxDetailsViewModel @Inject constructor(
  private val getBoxByShortIdInteractor: GetBoxByShortIdInteractor,
  private val getImagesByBoxIdInteractor: GetImagesByBoxIdInteractor,
  private val saveImagesInteractor: SaveImagesInteractor,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  val viewData = MutableStateFlow<BoxDetailsViewData?>(null)

  private val temporalBitmapList = mutableListOf<Bitmap>()

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
            }
          }
      }
    }
  }

  private fun getImages(uuid: String) {
    viewModelScope.launch(Dispatchers.IO) {
      getImagesByBoxIdInteractor.execute(uuid)
    }
  }

  fun save() {
    saveImagesInteractor.execute(
      viewData.value?.boxContent?.uuid.orEmpty(),
      viewData.value?.temporalBitmapList.orEmpty()
    )
    temporalBitmapList.clear()
  }

  fun addImages(bitmapList: List<Bitmap>) {
    temporalBitmapList.addAll(bitmapList)
    viewData.value = viewData.value?.copy(temporalBitmapList = temporalBitmapList)
  }
}

data class BoxDetailsViewData(
  val boxContent: BoxContent,
  val bitmapList: List<Bitmap>,
  val temporalBitmapList: List<Bitmap>
)

