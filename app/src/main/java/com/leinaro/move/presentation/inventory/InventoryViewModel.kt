package com.leinaro.move.presentation.inventory

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.Result
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.ResultParser
import com.leinaro.move.BoxContent
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListInteractor
import com.leinaro.move.domain.usecase.updateboxstatus.UpdateBoxStatusInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
  private val getBoxListInteractor: GetBoxListInteractor,
  private val updateBoxStatusInteractor: UpdateBoxStatusInteractor,
) : ViewModel() {
  val viewData = MutableStateFlow<InventoryViewData?>(null)

  var boxList: List<BoxContent>? = null

  fun onViewCreated() {
    getAllBoxes()
  }

  private fun getAllBoxes() {
    viewModelScope.launch(Dispatchers.IO) {
      getBoxListInteractor.execute()
        .collect { boxes ->
          boxList = boxes
          viewData.value = InventoryViewData(boxList = boxes)
        }
    }
  }

  fun handleDecodeInternally(rawResult: Result) {
    val result = ResultParser.parseResult(rawResult)
    if (result.type == ParsedResultType.URI) {
      val uri = Uri.parse(rawResult.text)
      if (uri.host == "move" && uri.scheme == "leinaro") {
/*        val item = PlaceholderContent.PlaceholderItem(
          uri.pathSegments.last(),
          uri.pathSegments.last(),
          uri.pathSegments.last(),
        )
        PlaceholderContent.ITEMS.add(item)
        PlaceholderContent.ITEM_MAP.put(item.id, item)*/

        registerBox(uri.pathSegments.last())
        /* val boxes = boxList?.map {
           if (it.uuid == uri.pathSegments.last()) {
             val box = it.copy(inventoried = true)
             registerBox(box)
             box
           } else {
             it
           }
         }
         boxes?.let {
           viewData.value = InventoryViewData(boxList = boxes)
         }*/

        //val box = boxList?.find { it.uuid == uri.pathSegments.last() }
        //box?.let {
        //registerBox(it.copy(inventoried = true))
//          viewData.value = InventoryViewData(boxList = boxes)
        //getAllBoxes()
        //}
      }
    }
  }

  private fun registerBox(uuid: String) {
    viewModelScope.launch(Dispatchers.IO) {
      updateBoxStatusInteractor.execute(uuid)
      getAllBoxes()
    }
  }
}

data class InventoryViewData(val boxList: List<BoxContent>)