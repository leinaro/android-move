package com.leinaro.move.presentation.inventorylist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.leinaro.move.R

class InventoryListFragment : Fragment() {

  companion object {
    fun newInstance() = InventoryListFragment()
  }

  private lateinit var viewModel: InventoryListViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.inventory_list_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(InventoryListViewModel::class.java)
    // TODO: Use the ViewModel
  }

}