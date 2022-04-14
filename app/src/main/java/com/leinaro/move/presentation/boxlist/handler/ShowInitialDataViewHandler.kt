package com.leinaro.move.presentation.boxlist.handler

import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.presentation.boxlist.BoxAdapter
import com.leinaro.move.presentation.boxlist.BoxListFragment
import com.leinaro.move.presentation.boxlist.BoxListViewData
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory

object ShowInitialDataViewHandler :
  ViewHandler<BoxListViewData.ShowInitialDataViewData, BaseViewModel<BoxListViewData>> {
  override fun BoxListViewData.ShowInitialDataViewData.perform(
    context: Any,
    viewModel: BaseViewModel<BoxListViewData>
  ) {
    if (context is BoxListFragment) {
      showInventoryData(context, inventory)
      val boxFilteredList = filter(boxList, query)
      showBoxesData(context, boxFilteredList)
    }
  }

  private fun showBoxesData(
    boxListFragment: BoxListFragment,
    boxList: List<BoxContent>,
  ) {
    if (boxList.isEmpty()) {
      boxListFragment.binding.emptyMessage.isVisible = true
    } else {
      boxListFragment.binding.itemNumber.text = "Cajas: ${boxList.count()}"
      with(boxListFragment.binding.list) {
        this.layoutManager = GridLayoutManager(
          boxListFragment.requireContext(), 2
        )
        this.adapter = BoxAdapter(
          boxList.toTypedArray(),
          boxListFragment
        )

      }
      boxListFragment.binding.list.isVisible = true
    }
  }

  private fun showInventoryData(boxListFragment: BoxListFragment, inventory: Inventory?) {
    boxListFragment.binding.inventoryBanner.setInventory(inventory)
  }

  private fun filter(models: List<BoxContent>, query: String): List<BoxContent> {
    if (query.isBlank()) return models
    val lowerCaseQuery = query.lowercase()
    val filteredModelList: MutableList<BoxContent> = ArrayList()
    for (model in models) {
      val text: String = model.toString().lowercase()
      if (text.contains(lowerCaseQuery)) {
        filteredModelList.add(model)
      }
    }
    return filteredModelList
  }
}
