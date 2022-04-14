package com.leinaro.architecture_tools

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun <T> AppCompatActivity.setObserver(viewModel: BaseViewModel<T>) {
  this.lifecycleScope.launch {
    viewModel.getViewData().filterNotNull()
      .collect {
        handleViewData(it, viewModel)
      }
    viewModel.getErrorViewData().filterNotNull().collect {
      handleViewData(it, viewModel)
    }
  }
  viewModel.getEventsFlow().filterNotNull()
    .onEach {
      handleViewEvent(it, viewModel)
    }
    .launchIn(this.lifecycleScope)
}

@Suppress("UNCHECKED_CAST")
fun <T, C> AppCompatActivity.handleViewData(viewDataState: ViewDataState<T>, viewModel: C) {
  (viewDataState.second as ViewHandler<T, C>).run {
    viewDataState.first.perform(
      context = this@handleViewData,
      viewModel = viewModel
    )
  }
}

@Suppress("UNCHECKED_CAST")
fun <T, C> AppCompatActivity.handleViewEvent(viewEventState: ViewEventState<T>, viewModel: C) {
  (viewEventState.second as ViewHandler<ViewEvent, C>).run {
    viewEventState.first.perform(
      context = this@handleViewEvent,
      viewModel = viewModel
    )
  }
}
/*
fun <T> AppCompatActivity.getNavigationResult(key: String = "result") =
  findNavController(this).currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> AppCompatActivity.setNavigationResult(result: T, key: String = "result") {
  findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}
*/

