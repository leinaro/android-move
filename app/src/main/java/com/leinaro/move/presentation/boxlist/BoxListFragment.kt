package com.leinaro.move.presentation.boxlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.leinaro.architecture_tools.getNavigationResult
import com.leinaro.architecture_tools.setObserver
import com.leinaro.move.R
import com.leinaro.move.databinding.FragmentBoxListBinding
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.presentation.inventorybanner.InventoryBannerListener
import com.leinaro.permissions.checkCameraPermission
import com.leinaro.permissions.getRequestPermissionLauncher
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoxListFragment : Fragment(), BoxAdapter.Listener, InventoryBannerListener,
  SearchView.OnQueryTextListener {

  private val viewModel: BoxListViewModel by viewModels()

  private val requestPermissionLauncher = getRequestPermissionLauncher(
    this, { navigateToInventoryFragment() }
  )

  private var _binding: FragmentBoxListBinding? = null

  val binding get() = _binding!!

  private val isLargeLayout by lazy {
    resources.getBoolean(R.bool.large_layout)
  }

  // region LifeCycle
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    setHasOptionsMenu(true); // Add this! (as above)
    _binding = FragmentBoxListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setObserver(viewModel)
    //  viewModel.onViewCreated()
    setListeners()
  }

  override fun onResume() {
    super.onResume()
    viewModel.onViewCreated()

  }
  // endregion

  // region BoxAdapter.Listener
  override fun onItemClickListener(boxContent: BoxContent?) {
    boxContent?.let {
      navigateToBoxDetailsActivity(it)
    }
  }
  // endregion

  // region InventoryBannerListener
  override fun onStartInventoryClick() {
    navigateToInventoryFragment()
  }

  override fun onInitInventoryClick() {
    showDialog()
  }
  // endregion

  // region private methods
  private fun setListeners() {
    binding.addBoxButton.setOnClickListener { navigateToCaptureActivity() }
    binding.scanButton.setOnClickListener { navigateToCaptureActivity() }
    binding.inventoryBanner.setBannerListener(this)
    getNavigationResult<Inventory>("inventory")?.observe(viewLifecycleOwner) { inventory ->
      binding.inventoryBanner.setInventory(inventory)
    }
  }

  private fun navigateToBoxDetailsActivity(boxContent: BoxContent) {
    val directions = BoxListFragmentDirections.navigateToBoxDetailsActivity(
      boxContent = boxContent,
      inventoryId = viewModel.inventoryId
    )
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
    val directions = BoxListFragmentDirections.navigateToCaptureActivity(viewModel.inventoryId)
    NavHostFragment.findNavController(this).navigate(directions)
  }

  private fun showDialog() {
    if (isLargeLayout) {
      // The device is using a large layout, so show the fragment as a dialog
      val directions = BoxListFragmentDirections.navigateToNewInventoryDialog()
      NavHostFragment.findNavController(this).navigate(directions)
    } else {
      val directions = BoxListFragmentDirections.navigateToNewInventoryDialogFragment()
      NavHostFragment.findNavController(this).navigate(directions)

      //     transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
      // To make it fullscreen, use the 'content' root view as the container
      // for the fragment, which is always the root view for the activity
      //   transaction
      //  .add(android.R.id.content, newFragment)
      // .addToBackStack(null)
      // .commit()
    }
  }
  // endregion

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    menu.clear()
    inflater.inflate(R.menu.box_list_menu, menu);
    val item: MenuItem = menu.findItem(R.id.action_search)
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)

    val searchView = item.actionView as SearchView
    searchView.setOnQueryTextListener(this)

    //getMenuInflater().inflate(R.menu.menu_main, menu)
    //val searchItem: MenuItem = menu.findItem(R.id.action_search)
    //val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
    //searchView.setOnQueryTextListener(this)
    //return true
  }

  override fun onQueryTextChange(query: String): Boolean {
    viewModel.filterBoxes(query)
    return true
  }

  override fun onQueryTextSubmit(query: String?): Boolean {
    return false
  }

/*  fun onEditStarted() {
    if (mBinding.editProgressBar.getVisibility() !== View.VISIBLE) {
      mBinding.editProgressBar.setVisibility(View.VISIBLE)
      mBinding.editProgressBar.setAlpha(0.0f)
    }
    if (mAnimator != null) {
      mAnimator.cancel()
    }
    mAnimator = ObjectAnimator.ofFloat<View>(mBinding.editProgressBar, View.ALPHA, 1.0f)
    mAnimator.setInterpolator(AccelerateDecelerateInterpolator())
    mAnimator.start()
    mBinding.recyclerView.animate().alpha(0.5f)
  }

  fun onEditFinished() {
    mBinding.recyclerView.scrollToPosition(0)
    mBinding.recyclerView.animate().alpha(1.0f)
    if (mAnimator != null) {
      mAnimator.cancel()
    }
    mAnimator = ObjectAnimator.ofFloat<View>(mBinding.editProgressBar, View.ALPHA, 0.0f)
    mAnimator.setInterpolator(AccelerateDecelerateInterpolator())
    mAnimator.addListener(object : AnimatorListenerAdapter() {
      private var mCanceled = false
      override fun onAnimationCancel(animation: Animator) {
        super.onAnimationCancel(animation)
        mCanceled = true
      }

      override fun onAnimationEnd(animation: Animator) {
        super.onAnimationEnd(animation)
        if (!mCanceled) {
          mBinding.editProgressBar.setVisibility(View.GONE)
        }
      }
    })
    mAnimator.start()
  }*/
}

