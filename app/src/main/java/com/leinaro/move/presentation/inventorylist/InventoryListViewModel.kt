package com.leinaro.move.presentation.inventorylist

import androidx.lifecycle.viewModelScope
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.move.domain.usecase.getinventorylist.GetInventoryListInteractor
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.presentation.inventorylist.handler.ShowInitialDataViewHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryListViewModel @Inject constructor(
  private val getInventoryListInteractor: GetInventoryListInteractor,
) : BaseViewModel<InventoryListViewData>() {
  fun onViewCreated() {
    viewModelScope.launch(Dispatchers.IO) {
      getInventoryListInteractor.execute()
        .collect { inventories ->
          setValue(
            InventoryListViewData.ShowInitialDataViewData(inventories),
            ShowInitialDataViewHandler
          )
        }
    }
  }
}

sealed class InventoryListViewData {
  data class ShowInitialDataViewData(
    val inventoryList: List<Inventory>
  ) : InventoryListViewData()
}