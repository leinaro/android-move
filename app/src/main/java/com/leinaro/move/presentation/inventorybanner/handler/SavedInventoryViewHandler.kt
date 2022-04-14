package com.leinaro.move.presentation.inventorybanner.handler

import androidx.navigation.fragment.findNavController
import com.leinaro.architecture_tools.BaseViewModel
import com.leinaro.architecture_tools.ViewHandler
import com.leinaro.architecture_tools.setNavigationResult
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.presentation.inventorybanner.NewInventoryDialogFragment
import com.leinaro.move.presentation.inventorybanner.NewInventoryViewData

object SavedInventoryViewHandler :
  ViewHandler<NewInventoryViewData.SavedInventory, BaseViewModel<NewInventoryViewData>> {
  override fun NewInventoryViewData.SavedInventory.perform(
    context: Any,
    viewModel: BaseViewModel<NewInventoryViewData>
  ) {
    if (context is NewInventoryDialogFragment) {
      finishWithResult(context, inventory)
    }
  }

  private fun finishWithResult(
    newInventoryDialogFragment: NewInventoryDialogFragment,
    inventory: Inventory
  ) {
    newInventoryDialogFragment.listener?.onInventorySaved(inventory)
    newInventoryDialogFragment.setNavigationResult(inventory, "inventory")
    newInventoryDialogFragment.findNavController().navigateUp()

    //context.requireActivity().finish()
    /*context.requireActivity().apply {
      val resultIntent = Intent()
      resultIntent.putIntegerArrayListExtra("ids", ids)
      setResult(Activity.RESULT_OK, resultIntent)
      finish()
    }*/
  }
}
