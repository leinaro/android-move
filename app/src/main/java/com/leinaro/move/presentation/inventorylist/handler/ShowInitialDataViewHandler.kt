package com.leinaro.move.presentation.inventorylist.handler

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.presentation.inventorylist.InventoryAdapter
import com.leinaro.move.presentation.inventorylist.InventoryListFragment
import com.leinaro.move.presentation.inventorylist.InventoryListViewData

object ShowInitialDataViewHandler :
  ViewHandler<InventoryListViewData.ShowInitialDataViewData, BaseViewModel<InventoryListViewData>> {
  override fun InventoryListViewData.ShowInitialDataViewData.perform(
    context: Any,
    viewModel: BaseViewModel<InventoryListViewData>
  ) {
    if (context is InventoryListFragment) {
      with(context.binding.list) {
        this.layoutManager = LinearLayoutManager(this.context)
        this.adapter = InventoryAdapter(
          inventoryList.toTypedArray(),
          context
        )
      }
      context.binding.list.isVisible = true
    }
  }

}
