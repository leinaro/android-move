package com.leinaro.move.presentation.boxlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.move.domain.usecase.getInventoryId.GetInventoryIdUseCase
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListInteractor
import com.leinaro.move.domain.usecase.getinventory.GetInventoryInteractor
import com.leinaro.move.presentation.boxlist.handler.ShowInitialDataViewHandler
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxListViewModel @Inject constructor(
  private val getInventoryInteractor: GetInventoryInteractor,
  private val getBoxListInteractor: GetBoxListInteractor,
  private val getInventoryIdUseCase: GetInventoryIdUseCase,
  private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<BoxListViewData>() {

  var inventoryId: Long = if (savedStateHandle.contains("inventoryId")) {
    savedStateHandle.get<Long>("inventoryId") ?: -1
  } else {
    getInventoryIdUseCase.execute()
  }

  private var inventory: Inventory? = null
  private var boxes: List<BoxContent>? = null

  private val inventoryFlow by lazy {
    if (inventoryId != -1L) {
      getInventoryInteractor.execute(inventoryId)
    } else {
      flowOf(null)
    }
  }

  fun onViewCreated() {
    viewModelScope.launch(Dispatchers.IO) {
      inventoryFlow.zip(getBoxListInteractor.execute(inventoryId)) { inventory, boxes ->
        inventory to boxes
      }
        .collect { (inventory, boxes) ->
          this@BoxListViewModel.inventory = inventory
          this@BoxListViewModel.boxes = boxes
          setValue(
            BoxListViewData.ShowInitialDataViewData(inventory = inventory, boxList = boxes),
            ShowInitialDataViewHandler
          )
        }
    }
  }

  fun filterBoxes(query: String) {
    setValue(
      BoxListViewData.ShowInitialDataViewData(
        inventory = inventory,
        boxList = boxes.orEmpty(),
        query
      ),
      ShowInitialDataViewHandler
    )
  }
}

sealed class BoxListViewData {
  data class ShowInitialDataViewData(
    val inventory: Inventory?,
    val boxList: List<BoxContent>,
    val query: String = ""
  ) : BoxListViewData()
}
