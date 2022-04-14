package com.leinaro.move.presentation.boxdetails.handler

import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.presentation.boxdetails.BoxDetailsActivity
import com.leinaro.move.presentation.boxdetails.BoxDetailsViewData

object BoxSavedViewHandler :
  ViewHandler<BoxDetailsViewData.BoxSaved, BaseViewModel<BoxDetailsViewData>> {
  override fun BoxDetailsViewData.BoxSaved.perform(
    context: Any,
    viewModel: BaseViewModel<BoxDetailsViewData>
  ) {
    if (context is BoxDetailsActivity) {
      context.finish()
    }
  }
}
