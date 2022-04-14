package com.leinaro.architecture_tools

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

fun <T> Fragment.setObserver(viewModel: BaseViewModel<T>) {
  viewLifecycleOwner.lifecycleScope.launch {
    viewModel.getViewData().filterNotNull()
      .collect {
        handleViewData(it, viewModel)
      }
    viewModel.getErrorViewData().filterNotNull().collect {
      handleViewData(it, viewModel)
    }
  }
  viewModel.getEventsFlow().filterNotNull().onEach {
    handleViewEvent(it, viewModel)
  }.launchIn(viewLifecycleOwner.lifecycleScope)
}

@Suppress("UNCHECKED_CAST")
fun <T, C> Fragment.handleViewData(viewDataState: ViewDataState<T>, viewModel: C) {
  (viewDataState.second as ViewHandler<T, C>).run {
    viewDataState.first.perform(
      context = this@handleViewData,
      viewModel = viewModel
    )
  }
}

@Suppress("UNCHECKED_CAST")
fun <T, C> Fragment.handleViewEvent(viewEventState: ViewEventState<T>, viewModel: C) {
  (viewEventState.second as ViewHandler<ViewEvent, C>).run {
    viewEventState.first.perform(
      context = this@handleViewEvent,
      viewModel = viewModel
    )
  }
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
  findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
  findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}


