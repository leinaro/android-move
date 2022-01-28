package com.leinaro.move.presentation.boxlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.leinaro.move.BoxContent
import com.leinaro.move.databinding.FragmentBoxListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoxListFragment : Fragment(), BoxAdapter.Listener {

  private var _binding: FragmentBoxListBinding? = null
  val binding get() = _binding!!

  private val viewModel: BoxListViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentBoxListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    this.lifecycleScope.launch {
      viewModel.viewData.filterNotNull()
        .collect {
          with(binding.list) {
            this.adapter = BoxAdapter(
              it.boxList.toTypedArray(),
              this@BoxListFragment
            )
          }
        }
    }
    viewModel.onViewCreated()
  }

  override fun onItemClickListener(boxContent: BoxContent?) {
    boxContent?.let {
      navigateToBoxDetailsActivity(it)
    }
  }

  private fun navigateToBoxDetailsActivity(boxContent: BoxContent) {
    val directions = BoxListFragmentDirections.navigateToBoxDetailsActivity(boxContent)
    NavHostFragment.findNavController(this).navigate(directions)
  }

}

