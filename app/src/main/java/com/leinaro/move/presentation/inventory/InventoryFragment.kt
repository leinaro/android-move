package com.leinaro.move.presentation.inventory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.leinaro.move.R
import com.leinaro.move.databinding.FragmentInventoryBinding
import com.leinaro.move.presentation.boxlist.BoxAdapter
import com.leinaro.move.presentation.capture.AmbientLightManager
import com.leinaro.move.presentation.capture.BeepManager
import com.leinaro.move.presentation.capture.CameraCaptureHandler
import com.leinaro.move.presentation.capture.CameraCaptureListener
import com.leinaro.move.presentation.capture.InactivityTimer
import com.leinaro.move.presentation.capture.camera.CameraManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.EnumSet

@AndroidEntryPoint
class InventoryFragment : Fragment(),
  SurfaceHolder.Callback, CameraCaptureListener {

  private var _binding: FragmentInventoryBinding? = null
  private val binding get() = _binding!!

  private val viewModel: InventoryViewModel by viewModels()

  private var cameraManager: CameraManager? = null
  private var handlerCamera: CameraCaptureHandler? = null

  private var hasSurface = false
  private var inactivityTimer: InactivityTimer? = null
  private var beepManager: BeepManager? = null
  private var ambientLightManager: AmbientLightManager? = null

  private val TAG = InventoryFragment::class.java.simpleName

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val window = requireActivity().window
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentInventoryBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView()
    collectData()
    setListeners()
    viewModel.onViewCreated()
  }

  private fun setListeners() {
    val sheetBehavior = BottomSheetBehavior.from(binding.inventoryBottomSheet.root)
    sheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
      override fun onStateChanged(bottomSheet: View, newState: Int) {}
      override fun onSlide(bottomSheet: View, slideOffset: Float) {
        //header_Arrow_Image.setRotation(slideOffset * 180)
      }
    })
  }

  private fun collectData() {
    this.lifecycleScope.launch {
      viewModel.viewData.filterNotNull()
        .collect { inventoryViewData ->
          if (inventoryViewData.boxList.isEmpty()) {

          } else {
            binding.inventoryBottomSheet.itemNumberInventoried.text =
              inventoryViewData.inventoried.toString()
            binding.inventoryBottomSheet.itemNumberTotal.text =
              inventoryViewData.total.toString()
            binding.inventoryBottomSheet.itemNumberPending.text =
              inventoryViewData.pending.toString()
            with(binding.inventoryBottomSheet.boxList) {
              this.adapter = BoxAdapter(
                inventoryViewData.boxList.toTypedArray(),
                //this@BoxListFragment
              )
            }
          }
        }
    }
  }

  override fun onResume() {
    super.onResume()
    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    setUpCameraManager()

    handlerCamera = null

    resetStatusView()

    ambientLightManager?.start(cameraManager)
    inactivityTimer?.onResume()

    val surfaceHolder = binding.previewView.holder
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      //  initCamera(surfaceHolder)
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this)
    }
  }

  // region implementation SurfaceHolder.Callback
  override fun surfaceCreated(holder: SurfaceHolder) {
    if (!hasSurface) {
      hasSurface = true
      initCamera(holder)
    }
  }

  override fun surfaceDestroyed(holder: SurfaceHolder) {
    hasSurface = false
  }

  override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    // do nothing
  }
  // endregion

  // region implementation CameraCaptureListener
  override fun drawViewfinder() {
    //TODO("Not yet implemented")
  }

  override fun returnScanResult(intent: Intent) {
    //TODO("Not yet implemented")
  }

  override fun decodeSucceeded(message: Message) {
    val bundle = message.data
    handleDecode((message.obj as Result))
  }

  private fun handleDecode(rawResult: Result) {
    inactivityTimer?.onActivity()

    beepManager?.playBeepSoundAndVibrate()

    viewModel.handleDecodeInternally(rawResult)
    handlerCamera?.sendEmptyMessageDelayed(R.id.restart_preview, 1000L)

    //updateAdapter()
  }

  override fun launchProductQuery(message: Message) {
    //TODO("Not yet implemented")
  }
  // endregion

  // region private methods
  private fun initView() {
    hasSurface = false
    inactivityTimer = InactivityTimer(this.requireActivity())
    beepManager = BeepManager(this.requireActivity())
    ambientLightManager = AmbientLightManager(this.requireContext())

    with(binding.inventoryBottomSheet.boxList) {
      layoutManager = GridLayoutManager(context, 2)
      //  adapter = BoxContentRecyclerViewAdapter(PlaceholderContent.ITEMS)
      adapter = BoxAdapter(
        emptyArray(),
        // it.boxList.toTypedArray(),
        //this@BoxListFragment
      )
    }
  }

  private fun setUpCameraManager() {
    cameraManager = CameraManager(this.requireContext())
  }

  private fun resetStatusView() {
    binding.statusView.setText(R.string.capture_code_message)
    binding.statusView.isVisible = true
    // lastResult = null
  }

  private fun initCamera(surfaceHolder: SurfaceHolder?) {
    checkNotNull(surfaceHolder) { "No SurfaceHolder provided" }
    if (cameraManager!!.isOpen) {
      Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?")
      return
    }
    try {
      cameraManager!!.openDriver(surfaceHolder)
      // Creating the handler starts the preview, which can also throw a RuntimeException.
      if (handlerCamera == null) {
        handlerCamera =
          CameraCaptureHandler(
            this,
            EnumSet.of(BarcodeFormat.QR_CODE),
            cameraManager!!,
          )
      }
      //decodeOrStoreSavedBitmap(null, null)
    } catch (ioe: IOException) {
      Log.w(TAG, ioe)
      //displayFrameworkBugMessageAndExit()
    } catch (e: RuntimeException) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializing camera", e)
      //displayFrameworkBugMessageAndExit()
    }
  }
  // endregion

  private val REQUEST_CAMERA_PERMISSION = 1
  private val FRAGMENT_DIALOG = "dialog"

  /*override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_CAMERA_PERMISSION -> {
        if (permissions.size != 1 || grantResults.size != 1) {
          throw java.lang.RuntimeException("Error on requesting camera permission.")
        }
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(
            this.context,
            R.string.camera_permission_not_granted,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }
  }*/
  // endregion
}