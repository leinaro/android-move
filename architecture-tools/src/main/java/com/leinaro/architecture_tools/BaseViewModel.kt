package com.leinaro.architecture_tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

  private val viewData = MutableStateFlow<ViewDataState<T>?>(null)
  private val errorViewData = MutableStateFlow<ViewDataState<ErrorViewData>?>(null)
  private val eventChannel = Channel<ViewEventState<T>>(Channel.BUFFERED)
  private val eventsFlow = eventChannel.receiveAsFlow()

  fun getViewData(): StateFlow<ViewDataState<T>?> = viewData
  fun getErrorViewData(): StateFlow<ViewDataState<ErrorViewData>?> = errorViewData
  fun getEventsFlow(): Flow<ViewEventState<T>?> = eventsFlow

  fun setValue(value: T, handler: ViewHandler<out T, out BaseViewModel<T>>) {
    viewModelScope.launch {
      viewData.emit(ViewDataState(value, handler))
    }
  }

  fun setErrorValue(
    value: ErrorViewData,
    handler: ViewHandler<ErrorViewData, BaseViewModel<ErrorViewData>>,
  ) {
    errorViewData.value = ViewDataState(value, handler)
  }

  open fun showError(throwable: Throwable) {
    //setErrorValue(DefaultError(throwable), ShowGeneralErrorViewHandler)
  }

  suspend fun sendEvent(
    viewEvent: ViewEvent,
    handler: ViewHandler<out ViewEvent, out BaseViewModel<T>>
  ) {
    eventChannel.send(ViewEventState(viewEvent, handler))
  }

}

sealed class ViewEvent {
  object NavigateToEmptyFragment : ViewEvent()
  data class ShowSnackBar(val text: String) : ViewEvent()
}