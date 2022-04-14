package com.leinaro.move.presentation.main.handler

import androidx.annotation.IdRes
import androidx.navigation.fragment.NavHostFragment
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.move.R
import com.leinaro.move.presentation.main.MainActivity
import com.leinaro.move.presentation.main.MainViewData

object ShowInitialDataViewHandler :
  ViewHandler<MainViewData.ShowInitialDataViewData, BaseViewModel<MainViewData>> {
  override fun MainViewData.ShowInitialDataViewData.perform(
    context: Any,
    viewModel: BaseViewModel<MainViewData>
  ) {
    if (context is MainActivity) {
      if (inventoryList.isEmpty()) {
        setNavigationGraph(context, R.id.boxListFragment)
      } else {
        setNavigationGraph(context, R.id.inventoryListFragment)
      }
    }
  }

  private fun setNavigationGraph(mainActivity: MainActivity, @IdRes startDestinationId: Int) {
    val navHostFragment = mainActivity.supportFragmentManager.findFragmentById(
      R.id.nav_host_fragment
    ) as NavHostFragment
    val navController = navHostFragment.navController
    val navGraph = navController.navInflater.inflate(R.navigation.main_nav_graph)
    navGraph.setStartDestination(startDestinationId)
    navController.graph = navGraph
  }
}
