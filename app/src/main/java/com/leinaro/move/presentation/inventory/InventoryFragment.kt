package com.leinaro.move.presentation.inventory

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import com.leinaro.move.R
import com.leinaro.move.databinding.FragmentInventoryBinding
import com.leinaro.move.presentation.capture.AmbientLightManager
import com.leinaro.move.presentation.capture.BeepManager
import com.leinaro.move.presentation.capture.CameraCaptureHandler
import com.leinaro.move.presentation.capture.FinishListener
import com.leinaro.move.presentation.capture.ScanFromWebPageManager
import com.leinaro.move.presentation.capture.camera.CameraManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class InventoryFragment : Fragment() { //, SurfaceHolder.Callback  {
  private var _binding: FragmentInventoryBinding? = null
  val binding get() = _binding!!

  //  private val viewModel: MainFragmentViewModel by viewModels()

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    Log.e("iarl", "isGranted $isGranted")
    if (isGranted) {
      // Permission is granted. Continue the action or workflow in your
      // app.
    } else {
      // TODO:
      //finish()
      // Explain to the user that the feature is unavailable because the
      // features requires a permission that the user has denied. At the
      // same time, respect the user's decision. Don't link to system
      // settings in an effort to convince the user to change their
      // decision.
    }
  }

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

 /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (!haveCameraPermission()) {
      requestCameraPermission()
      return
    }
    initView()
  }

  var cameraManager: CameraManager? = null
  var handlerCamera: CameraCaptureHandler? = null
  private var lastResult: Result? = null


  private var sourceUrl: String? = null
  private var scanFromWebPageManager: ScanFromWebPageManager? = null
  private var decodeFormats: Collection<BarcodeFormat?>? = null
  private var characterSet: String? = null

  override fun onResume() {
    super.onResume()
    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    cameraManager = CameraManager(this.requireActivity().application)
    binding.viewfinderView.setCameraManager(cameraManager)

    handlerCamera = null
    lastResult = null

    resetStatusView()

    beepManager?.updatePrefs()
    ambientLightManager?.start(cameraManager)


    sourceUrl = null
    scanFromWebPageManager = null
    decodeFormats = null
    characterSet = null

    val surfaceHolder = binding.previewView.holder
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder)
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this)
    }
  }

  private fun haveCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      this.requireContext(),
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestCameraPermission() {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
        Manifest.permission.CAMERA
      ) -> {
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected. In this UI,
        // include a "cancel" or "no thanks" button that allows the user to
        // continue using your app without granting the permission.
        // TODO: showInContextUI(...)
      }
      else -> {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }
  }

  private var hasSurface = false
  private var beepManager: BeepManager? = null
  private var ambientLightManager: AmbientLightManager? = null

  private fun initView() {
    hasSurface = false
    beepManager = BeepManager(this.requireActivity())
    ambientLightManager = AmbientLightManager(this.requireContext())
  }

  private fun resetStatusView() {
    Log.e("iarl", "resetStatusView")

    binding.resultView.isVisible = false
    binding.statusView.setText(R.string.capture_code_message)
    binding.statusView.isVisible = true
    binding.viewfinderView.isVisible = true
    lastResult = null
  }

  private var decodeHints: Map<DecodeHintType, *>? = null


  private fun initCamera(surfaceHolder: SurfaceHolder?) {
    Log.e("iarl", "initCamera")

    checkNotNull(surfaceHolder) { "No SurfaceHolder provided" }
    if (cameraManager!!.isOpen) {
      Log.w("TAG", "initCamera() while already open -- late SurfaceView callback?")
      return
    }
    try {
      cameraManager!!.openDriver(surfaceHolder)
      // Creating the handler starts the preview, which can also throw a RuntimeException.
      if (handlerCamera == null) {
        handlerCamera =
          CameraCaptureHandler(this, decodeFormats, decodeHints, characterSet, cameraManager!!)
      }
      decodeOrStoreSavedBitmap(null, null)
    } catch (ioe: IOException) {
      Log.w("TAG", ioe)
      displayFrameworkBugMessageAndExit()
    } catch (e: RuntimeException) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w("TAG", "Unexpected error initializing camera", e)
      displayFrameworkBugMessageAndExit()
    }
  }

  private var savedResultToShow: Result? = null

  private fun decodeOrStoreSavedBitmap(bitmap: Bitmap?, result: Result?) {
    // Bitmap isn't used yet -- will be used soon
    Log.e("iarl", "decodeOrStoreSavedBitmap")
    if (handlerCamera == null) {
      savedResultToShow = result
    } else {
      if (result != null) {
        savedResultToShow = result
      }
      if (savedResultToShow != null) {
        val message = Message.obtain(handlerCamera, R.id.decode_succeeded, savedResultToShow)
        handlerCamera!!.sendMessage(message)
      }
      savedResultToShow = null
    }
  }

  override fun surfaceCreated(holder: SurfaceHolder) {
    Log.e("iarl", "surfaceCreated")
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


  private fun displayFrameworkBugMessageAndExit() {
    Log.e("iarl", "displayFrameworkBugMessageAndExit")

    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.app_name))
    builder.setMessage(getString(R.string.msg_camera_framework_bug))
    builder.setPositiveButton(R.string.button_ok, FinishListener(this))
    builder.setOnCancelListener(FinishListener(this))
    builder.show()
  }*/
}