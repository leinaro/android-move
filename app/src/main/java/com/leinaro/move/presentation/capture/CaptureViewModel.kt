package com.leinaro.move.presentation.capture

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.zxing.Result
import com.leinaro.move.presentation.capture.result.ResultHandler
import com.leinaro.move.presentation.capture.result.URIResultHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

private const val host = "move"
private const val scheme = "leinaro"

@HiltViewModel
class CaptureViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  val viewData = MutableStateFlow<CaptureViewData?>(null)

  // Put up our own UI for how to handle the decoded contents.
  fun handleDecodeInternally(
    rawResult: Result,
    resultHandler: ResultHandler,
    barcode: Bitmap?
  ) {
    Log.e("iarl", "handleDecodeInternally $rawResult")
    Log.e("iarl", "handleDecodeInternally $resultHandler")

    if (resultHandler is URIResultHandler) {
      val uri = Uri.parse(rawResult.text)
      if (uri.host == host && uri.scheme == scheme) {
        viewData.value = CaptureViewData(uri)
      }
    }
    //showQRInfo(rawResult, resultHandler)
  }

}

data class CaptureViewData(
  val uri: Uri
)
