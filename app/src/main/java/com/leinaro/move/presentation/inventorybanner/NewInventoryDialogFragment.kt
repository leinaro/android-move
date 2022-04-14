package com.leinaro.move.presentation.inventorybanner

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.leinaro.architecture_tools.setObserver
import com.leinaro.move.databinding.FragmentNewInventoryDialogBinding
import com.leinaro.move.domain.data.Inventory
import com.leinaro.validatable_fields.bindTextInputLayout
import dagger.hilt.android.AndroidEntryPoint

interface NewInventoryListener {
  fun onInventorySaved(inventory: Inventory)
}

@AndroidEntryPoint
class NewInventoryDialogFragment : DialogFragment() {

  private val viewModel: NewInventoryViewModel by viewModels()

  lateinit var binding: FragmentNewInventoryDialogBinding

  var listener: NewInventoryListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    listener = context as? NewInventoryListener
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    // Inflate the layout to use as dialog or embedded fragment
    binding = FragmentNewInventoryDialogBinding.inflate(LayoutInflater.from(context))
    //  val view = inflater.inflate(R.layout.fragment_new_inventory_dialog, container, false)
    setListener()
    setObserver(viewModel)
    bindFieldsValidator()

    return binding.root//view
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // The only reason you might override this method when using onCreateView() is
    // to modify any dialog characteristics. For example, the dialog includes a
    // title by default, but your custom layout might not need it. So here you can
    // remove the dialog title, but you must call the superclass to get the Dialog.
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    return dialog
  }

  private fun bindFieldsValidator() {
    viewModel.originValidator.bindTextInputLayout(
      this,
      binding.textFieldOrigin,
      isOptional = true,
    )
    viewModel.arrivalValidator.bindTextInputLayout(
      this,
      binding.textFieldDestiny,
    )
  }

  private fun setListener() {
    binding.saveButton.setOnClickListener {
      viewModel.save()
    }
  }
}