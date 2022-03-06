package com.leinaro.move.presentation.boxlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.databinding.FragmentBoxListBinding
import com.leinaro.permissions.checkCameraPermission
import com.leinaro.permissions.getRequestPermissionLauncher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxListFragment : Fragment(), BoxAdapter.Listener {

  private val viewModel: BoxListViewModel by viewModels()

  private val requestPermissionLauncher = getRequestPermissionLauncher(
    this, { navigateToInventoryFragment() }
  )

  private var _binding: FragmentBoxListBinding? = null

  val binding get() = _binding!!

  // region LifeCycle
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBoxListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setObservers()
    viewModel.onViewCreated()
    setListeners()
  }
  // endregion

  // region BoxAdapter.Listener
  override fun onItemClickListener(boxContent: BoxContent?) {
    boxContent?.let {
      navigateToBoxDetailsActivity(it)
    }
  }
  // end region

  // region private methods
  private fun setListeners() {
    binding.addBoxButton.setOnClickListener { navigateToCaptureActivity() }
    binding.scanButton.setOnClickListener { navigateToCaptureActivity() }
    binding.inventoryButton.setOnClickListener { navigateToInventoryFragment() }
  }

  private fun setObservers() {
    this.lifecycleScope.launch {
      viewModel.viewData.filterNotNull()
        .collect { viewData ->
          if (viewData.boxList.isEmpty()) {
            binding.emptyMessage.isVisible = true
          } else {
            binding.itemNumber.text = "Cajas: ${viewData.boxList.count()}"
            with(binding.list) {
              this.layoutManager = GridLayoutManager(
                this@BoxListFragment.requireContext(), 2
              )
              this.adapter = BoxAdapter(
                viewData.boxList.toTypedArray(),
                this@BoxListFragment
              )
            }
            binding.list.isVisible = true
          }
        }
    }
  }

  private fun navigateToBoxDetailsActivity(boxContent: BoxContent) {
    val directions = BoxListFragmentDirections.navigateToBoxDetailsActivity(boxContent)
    NavHostFragment.findNavController(this).navigate(directions)
  }

  private fun navigateToInventoryFragment() {
    if (!checkCameraPermission(this, requestPermissionLauncher)) {
      return
    }
    val directions = BoxListFragmentDirections.navigateToInventoryFragment()
    NavHostFragment.findNavController(this).navigate(directions)
  }

  private fun navigateToCaptureActivity() {
    if (!checkCameraPermission(this, requestPermissionLauncher)) {
      return
    }
    val directions = BoxListFragmentDirections.navigateToCaptureActivity()
    NavHostFragment.findNavController(this).navigate(directions)
  }
  // endregion
}

