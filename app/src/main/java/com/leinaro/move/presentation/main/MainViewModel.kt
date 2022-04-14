package com.leinaro.move.presentation.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.move.domain.usecase.getinvetories.GetInventoriesInteractor
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.presentation.main.handler.ShowInitialDataViewHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val getInventoriesInteractor: GetInventoriesInteractor,
  private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<MainViewData>() {
  fun onCreate() {
    viewModelScope.launch(Dispatchers.IO) {
      getInventoriesInteractor.execute()
        .collect { inventoryList ->
          setValue(
            MainViewData.ShowInitialDataViewData(inventoryList),
            ShowInitialDataViewHandler
          )
        }
    }
  }
}

sealed class MainViewData {
  data class ShowInitialDataViewData(val inventoryList: List<Inventory>) : MainViewData()
}
