package com.leinaro.move.presentation.inventorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.leinaro.architecture_tools.setObserver
import com.leinaro.move.databinding.InventoryListFragmentBinding
import com.leinaro.move.domain.data.Inventory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryListFragment : Fragment(), InventoryAdapter.Listener {

  private val viewModel: InventoryListViewModel by viewModels()

  private var _binding: InventoryListFragmentBinding? = null

  val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = InventoryListFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setObserver(viewModel)
    viewModel.onViewCreated()
    setListeners()
  }

  // region private methods

  private fun setListeners() {
    // binding.addBoxButton.setOnClickListener { navigateToCaptureActivity() }
    // binding.scanButton.setOnClickListener { navigateToCaptureActivity() }
    // binding.inventoryButton.setOnClickListener { navigateToInventoryFragment() }
  }

  override fun onItemClickListener(inventory: Inventory?) {
    navigateToBoxListFragment(inventory?.id ?: -1)
  }
  // end region

  private fun navigateToBoxListFragment(id: Long) {
    val directions = InventoryListFragmentDirections.navigateToBoxListFragment(id)
    NavHostFragment.findNavController(this).navigate(directions)
  }
}