package com.leinaro.move.presentation.boxdetails.handler

import android.graphics.Bitmap
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.presentation.boxdetails.BoxDetailsActivity
import com.leinaro.move.presentation.boxdetails.BoxDetailsViewData
import com.leinaro.move.presentation.boxdetails.CustomAdapter
import com.leinaro.move.domain.data.BoxContent

object ShowInitialDataViewHandler :
  ViewHandler<BoxDetailsViewData.ShowInitialDataViewData, BaseViewModel<BoxDetailsViewData>> {
  override fun BoxDetailsViewData.ShowInitialDataViewData.perform(
    context: Any,
    viewModel: BaseViewModel<BoxDetailsViewData>
  ) {
    if (context is BoxDetailsActivity) {
      showBoxContent(context, boxContent)
      showImages(context, bitmapList.toMutableList(), temporalBitmapList)
    }
  }

  private fun showImages(
    boxDetailsActivity: BoxDetailsActivity,
    bitmapList: MutableList<Bitmap>,
    temporalBitmapList: List<Bitmap>
  ) {
    with(boxDetailsActivity.binding.photos) {
      bitmapList.addAll(temporalBitmapList)
      this.adapter = CustomAdapter(bitmapList.toTypedArray())
    }
  }

  private fun showBoxContent(boxDetailsActivity: BoxDetailsActivity, boxContent: BoxContent) {
    boxDetailsActivity.binding.textFieldQrCode.text = boxContent.uuid
    boxDetailsActivity.binding.textFieldLocation.editText?.setText(boxContent.location)
    boxDetailsActivity.binding.textFieldDescription.editText?.setText(boxContent.description)
  }

}