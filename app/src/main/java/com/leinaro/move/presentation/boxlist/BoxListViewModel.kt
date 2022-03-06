package com.leinaro.move.presentation.boxlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoxListViewModel @Inject constructor(
  private val getBoxListInteractor: GetBoxListInteractor,
) : ViewModel() {
  val viewData = MutableStateFlow<BoxListViewData?>(null)

  fun onViewCreated() {
    viewModelScope.launch(Dispatchers.IO) {
      getBoxListInteractor.execute()
        .collect { boxes ->
          viewData.value = BoxListViewData(boxList = boxes)
        }
    }
  }
}

data class BoxListViewData(val boxList: List<BoxContent>)
