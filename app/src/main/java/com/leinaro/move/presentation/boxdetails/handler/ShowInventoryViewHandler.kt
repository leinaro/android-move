package com.leinaro.move.presentation.boxdetails.handler

import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.presentation.boxdetails.BoxDetailsActivity
import com.leinaro.move.presentation.boxdetails.BoxDetailsViewData
import com.leinaro.move.domain.data.Inventory

object ShowInventoryViewHandler :
  ViewHandler<BoxDetailsViewData.ShowInventoryViewData, BaseViewModel<BoxDetailsViewData>> {
  override fun BoxDetailsViewData.ShowInventoryViewData.perform(
    context: Any,
    viewModel: BaseViewModel<BoxDetailsViewData>
  ) {
    if (context is BoxDetailsActivity) {
      showInventoryData(context, inventory)
    }
  }

  private fun showInventoryData(boxDetailsActivity: BoxDetailsActivity, inventory: Inventory?) {
    boxDetailsActivity.binding.textFieldInventory.text = "${inventory?.origin} - ${inventory?.destination}"
  }

}
