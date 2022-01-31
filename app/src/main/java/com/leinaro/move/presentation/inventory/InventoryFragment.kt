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
import com.google.zxing.BarcodeFormat
import com.leinaro.move.R
import com.leinaro.move.databinding.FragmentInventoryBinding
import com.leinaro.move.presentation.capture.CameraCaptureHandler
import com.leinaro.move.presentation.capture.CameraCaptureListener
import com.leinaro.move.presentation.capture.ViewfinderResultPointCallback
import com.leinaro.move.presentation.capture.camera.CameraManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.EnumSet

@AndroidEntryPoint
class InventoryFragment : Fragment(), SurfaceHolder.Callback, CameraCaptureListener {
  private var _binding: FragmentInventoryBinding? = null
  private val binding get() = _binding!!

  private var cameraManager: CameraManager? = null
  private var hasSurface = false
  private val TAG = InventoryFragment::class.java.simpleName
  private var handlerCamera: CameraCaptureHandler? = null

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
  }

  override fun onResume() {
    super.onResume()
    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    setUpCameraManager()

    handlerCamera = null
    //lastResult = null

    resetStatusView()

    //ambientLightManager?.start(cameraManager)
    //inactivityTimer?.onResume()

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
    //TODO("Not yet implemented")
  }

  override fun launchProductQuery(message: Message) {
    //TODO("Not yet implemented")
  }
  // endregion

  // region private methods
  private fun initView() {
    //hasSurface = false
    //inactivityTimer = InactivityTimer(this)
    //beepManager = BeepManager(this)
    //ambientLightManager = AmbientLightManager(this)
  }

  private fun setUpCameraManager() {
    cameraManager = CameraManager(this.requireContext())
    binding.viewfinderView.setCameraManager(cameraManager)
  }

  private fun resetStatusView() {
    binding.statusView.setText(R.string.capture_code_message)
    binding.statusView.isVisible = true
    binding.viewfinderView.isVisible = true
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
            ViewfinderResultPointCallback(binding.viewfinderView)
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

}