/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leinaro.move.presentation.capture

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.provider.Browser
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import com.google.zxing.ResultPoint
import com.leinaro.move.BoxDetailsActivity
import com.leinaro.move.R
import com.leinaro.move.databinding.ActivityCaptureBinding
import com.leinaro.move.presentation.capture.camera.CameraManager
import com.leinaro.move.presentation.capture.result.ResultHandler
import com.leinaro.move.presentation.capture.result.ResultHandlerFactory
import com.leinaro.move.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.util.EnumSet

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
@AndroidEntryPoint
class CaptureActivity : AppCompatActivity(), SurfaceHolder.Callback/*, CameraCaptureListener*/ {

  val binding by viewBinding(ActivityCaptureBinding::inflate)

  private val viewModel: CaptureViewModel by viewModels()

  var cameraManager: CameraManager? = null
  var handlerCamera: CameraCaptureHandler? = null

  private var savedResultToShow: Result? = null
  private var lastResult: Result? = null

  private var hasSurface = false

  private var sourceUrl: String? = null
  private var scanFromWebPageManager: ScanFromWebPageManager? = null
  private var decodeHints: Map<DecodeHintType, *>? = null
  private var characterSet: String? = null

  private var inactivityTimer: InactivityTimer? = null
  private var beepManager: BeepManager? = null
  private var ambientLightManager: AmbientLightManager? = null

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

  public override fun onCreate(icicle: Bundle?) {
    super.onCreate(icicle)

    val window = window
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    setContentView(binding.root)

    if (!haveCameraPermission()) {
      requestCameraPermission()
      return
    }

    initView()

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    setObserver()
  }

  private fun haveCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      this,
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

  private fun initView() {
    hasSurface = false
    inactivityTimer = InactivityTimer(this)
    beepManager = BeepManager(this)
    ambientLightManager = AmbientLightManager(this)

  }

  private fun setObserver() {
    this.lifecycleScope.launch {
      viewModel.viewData.filterNotNull()
        .collect {
          navigateToBoxDetailsActivity(it.uri)
        }
    }
  }

  private fun navigateToBoxDetailsActivity(uri: Uri) {
    val intent = Intent(this@CaptureActivity, BoxDetailsActivity::class.java)
    intent.data = uri
    intent.action = Intent.ACTION_DEFAULT
    startActivity(intent)
  }

  override fun onResume() {
    super.onResume()

    // historyManager must be initialized here to update the history preference
    //historyManager = HistoryManager(this)
    // historyManager?.trimHistory()

    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    cameraManager = CameraManager(application)
    binding.viewfinderView.setCameraManager(cameraManager)

    handlerCamera = null
    lastResult = null

    resetStatusView()

    beepManager?.updatePrefs()
    ambientLightManager?.start(cameraManager)
    inactivityTimer?.onResume()

    val intent = intent

    sourceUrl = null
    scanFromWebPageManager = null
    characterSet = null

    if (intent != null) {
      val action = intent.action
      val dataString = intent.dataString
      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET)
    }
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

  override fun onPause() {
    Log.e("iarl", "onPause")

    if (handlerCamera != null) {
      handlerCamera?.quitSynchronously()
      handlerCamera = null
    }
    inactivityTimer!!.onPause()
    ambientLightManager!!.stop()
    beepManager!!.close()
    cameraManager!!.closeDriver()
    //historyManager = null; // Keep for onActivityResult
    if (!hasSurface) {
      val surfaceHolder = binding.previewView.holder
      surfaceHolder.removeCallback(this)
    }
    super.onPause()
  }

  override fun onDestroy() {
    inactivityTimer?.shutdown()
    super.onDestroy()
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    when (keyCode) {
      KeyEvent.KEYCODE_BACK -> {
        if (lastResult != null) {
          restartPreviewAfterDelay(0L)
          return true
        }
      }
      KeyEvent.KEYCODE_FOCUS, KeyEvent.KEYCODE_CAMERA ->         // Handle these events so they don't launch the Camera app
        return true
      KeyEvent.KEYCODE_VOLUME_DOWN -> {
        cameraManager?.setTorch(false)
        return true
      }
      KeyEvent.KEYCODE_VOLUME_UP -> {
        cameraManager?.setTorch(true)
        return true
      }
    }
    return super.onKeyDown(keyCode, event)
  }

  /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val menuInflater = menuInflater
    menuInflater.inflate(R.menu.capture, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intents.FLAG_NEW_DOC)
    when (item.itemId) {
      R.id.menu_history -> {
        intent.setClassName(this, HistoryActivity::class.java.name)
        startActivityForResult(intent, HISTORY_REQUEST_CODE)
      }
      R.id.menu_settings -> {
        intent.setClassName(this, PreferencesActivity::class.java.name)
        startActivity(intent)
      }
      R.id.menu_help -> {
        intent.setClassName(this, HelpActivity::class.java.name)
        startActivity(intent)
      }
      else -> return super.onOptionsItemSelected(item)
    }
    return true
  }*/

  /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     super.onActivityResult(requestCode, resultCode, data)
     Log.e("iarl", "onActivityResult")

     if (resultCode == RESULT_OK && requestCode == HISTORY_REQUEST_CODE && historyManager != null) {
       val itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1)
       if (itemNumber >= 0) {
         val historyItem = historyManager?.buildHistoryItem(itemNumber)
         decodeOrStoreSavedBitmap(null, historyItem.result)
       }
     }
   }  */

  private fun decodeOrStoreSavedBitmap(bitmap: Bitmap?, result: Result?) {
    // Bitmap isn't used yet -- will be used soon
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

  /**
   * A valid code has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param scaleFactor amount by which thumbnail was scaled
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
   fun handleDecode(rawResult: Result, bitmap: Bitmap?, scaleFactor: Float) {
    Log.e("iarl", "handleDecode")

    inactivityTimer?.onActivity()
    lastResult = rawResult

    val resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult)

    /*if (bitmap != null) {
      historyManager?.addHistoryItem(rawResult, resultHandler)
      // Then not from history, so beep/vibrate and we have an image to draw on
      beepManager?.playBeepSoundAndVibrate()
      //drawResultPoints(bitmap, scaleFactor, rawResult)
    } */

    viewModel.handleDecodeInternally(rawResult, resultHandler, bitmap)
  }

 /* override fun returnScanResult(resultCode: Int, intent: Intent) {
    Log.e("iarl", "returnscanresult")

    this.setResult(resultCode, intent)
    this.finish()
  }

  override fun launchProductQuery(url : String) {
    Log.e("iarl", "launchproductquery")

    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intents.FLAG_NEW_DOC)
    intent.data = Uri.parse(url)

    val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

    var browserPackageName: String? = null
    if (resolveInfo?.activityInfo != null) {
      browserPackageName = resolveInfo.activityInfo.packageName
    }

    // Needed for default Android browser / Chrome only apparently
    if (browserPackageName != null) {
      when (browserPackageName) {
        "com.android.browser", "com.android.chrome" -> {
          intent.setPackage(browserPackageName)
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName)
        }
      }
    }

    try {
      this.startActivity(intent)
    } catch (ignored: ActivityNotFoundException) {
      Log.w(TAG, "Can't find anything to handle VIEW of URI")
    }
  }

  override fun restartPreviewAndDecode(handler: Handler?) {
    Log.e("iarl", "restartPreviewAndDecode")

    cameraManager?.requestPreviewFrame(handler, R.id.decode)
    this.drawViewfinder()
  }*/

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param bitmap   A bitmap of the captured image.
   * @param scaleFactor amount by which thumbnail was scaled
   * @param rawResult The decoded results which contains the points to draw.
   */
  /*private fun drawResultPoints(bitmap: Bitmap, scaleFactor: Float, rawResult: Result) {
    Log.e("iarl", "drawResultPoints")
    val points = rawResult.resultPoints
    if (points != null && points.size > 0) {
      val canvas = Canvas(bitmap)
      val paint = Paint()
      paint.color = ContextCompat.getColor(this, R.color.result_points)
      if (points.size == 2) {
        paint.strokeWidth = 4.0f
        drawLine(canvas, paint, points[0], points[1], scaleFactor)
      } else if (points.size == 4 &&
        (rawResult.barcodeFormat == BarcodeFormat.UPC_A ||
            rawResult.barcodeFormat == BarcodeFormat.EAN_13)
      ) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1], scaleFactor)
        drawLine(canvas, paint, points[2], points[3], scaleFactor)
      } else {
        paint.strokeWidth = 10.0f
        for (point in points) {
          if (point != null) {
            canvas.drawPoint(scaleFactor * point.x, scaleFactor * point.y, paint)
          }
        }
      }
    }
  }  */

  private fun showQRInfo(rawResult: Result, resultHandler: ResultHandler) {
    binding.statusView.visibility = View.GONE
    binding.viewfinderView.visibility = View.GONE
    binding.resultView.visibility = View.VISIBLE
    /* if (barcode == null) {
       binding.barcodeImageView.setImageBitmap(
         BitmapFactory.decodeResource(
           resources,
           R.drawable.ic_launcher_foreground
         )
       )
     } else {
       binding.barcodeImageView.setImageBitmap(barcode)
     }*/

    binding.formatTextView.text = rawResult.barcodeFormat.toString()
    binding.typeTextView.text = resultHandler.type.toString()
    val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    binding.timeTextView.text = formatter.format(rawResult.timestamp)
    binding.metaTextView.visibility = View.GONE
    binding.metaTextViewLabel.visibility = View.GONE

    val metadata = rawResult.resultMetadata
    if (metadata != null) {
      val metadataText = StringBuilder(20)
      for ((key, value) in metadata) {
        if (DISPLAYABLE_METADATA_TYPES.contains(key)) {
          metadataText.append(value).append('\n')
        }
      }
      if (metadataText.length > 0) {
        metadataText.setLength(metadataText.length - 1)
        binding.metaTextView.text = metadataText
        binding.metaTextView.visibility = View.VISIBLE
        binding.metaTextViewLabel.visibility = View.VISIBLE
      }
    }

    val displayContents = resultHandler.displayContents
    binding.contentsTextView.text = displayContents
    val scaledSize = Math.max(22, 32 - displayContents.length / 4)
    binding.contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize.toFloat())
    binding.contentsSupplementTextView.text = ""
    binding.contentsSupplementTextView.setOnClickListener(null)
    /*if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
        PreferencesActivity.KEY_SUPPLEMENTAL, true
      )
    ) {
      SupplementalInfoRetriever.maybeInvokeRetrieval(
        binding.contentsSupplementTextView,
        resultHandler.result,
        historyManager,
        this
      )
    } */
  }

  private fun initCamera(surfaceHolder: SurfaceHolder?) {
    Log.e("iarl", "initCamera ")

    checkNotNull(surfaceHolder) { "No SurfaceHolder provided" }
    if (cameraManager!!.isOpen) {
      Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?")
      return
    }
    try {
      cameraManager!!.openDriver(surfaceHolder)
      // Creating the handler starts the preview, which can also throw a RuntimeException.
      if (handlerCamera == null) {
        Log.e("iarl", "init handler Camera ")

       /* var decodeThread: DecodeThread = DecodeThread(
          handlerCamera, cameraManager, EnumSet.of(BarcodeFormat.QR_CODE), decodeHints, characterSet,
          ViewfinderResultPointCallback(binding.viewfinderView)
        )*/

        handlerCamera =
          CameraCaptureHandler(
            this,
//            this,
            EnumSet.of(BarcodeFormat.QR_CODE),
            decodeHints,
            characterSet,
      //      decodeThread,
            cameraManager!!
          )
      }
      decodeOrStoreSavedBitmap(null, null)
    } catch (ioe: IOException) {
      Log.w(TAG, ioe)
      displayFrameworkBugMessageAndExit()
    } catch (e: RuntimeException) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializing camera", e)
      displayFrameworkBugMessageAndExit()
    }
  }

  private fun displayFrameworkBugMessageAndExit() {
    Log.e("iarl", "displayFrameworkBugMessageAndExit")

    val builder = AlertDialog.Builder(this)
    builder.setTitle(getString(R.string.app_name))
    builder.setMessage(getString(R.string.msg_camera_framework_bug))
    builder.setPositiveButton(R.string.button_ok, FinishListener(this))
    builder.setOnCancelListener(FinishListener(this))
    builder.show()
  }

  fun restartPreviewAfterDelay(delayMS: Long) {
    Log.e("iarl", "restartPreviewAfterDelay")

    if (handlerCamera != null) {
      handlerCamera!!.sendEmptyMessageDelayed(R.id.restart_preview, delayMS)
    }
    resetStatusView()
  }

  private fun resetStatusView() {
    Log.e("iarl", "resetStatusView")

    binding.resultView.isVisible = false
    binding.statusView.setText(R.string.capture_code_message)
    binding.statusView.isVisible = true
    binding.viewfinderView.isVisible = true
    lastResult = null
  }

 fun drawViewfinder() {
    binding.viewfinderView.drawViewfinder()
  }

  companion object {
    private val TAG = CaptureActivity::class.java.simpleName

    //  private const val HISTORY_REQUEST_CODE = 0x0000bacc
    private val DISPLAYABLE_METADATA_TYPES: Collection<ResultMetadataType> = EnumSet.of(
      ResultMetadataType.ISSUE_NUMBER,
      ResultMetadataType.SUGGESTED_PRICE,
      ResultMetadataType.ERROR_CORRECTION_LEVEL,
      ResultMetadataType.POSSIBLE_COUNTRY
    )

    private fun drawLine(
      canvas: Canvas,
      paint: Paint,
      a: ResultPoint?,
      b: ResultPoint?,
      scaleFactor: Float
    ) {
      if (a != null && b != null) {
        canvas.drawLine(
          scaleFactor * a.x,
          scaleFactor * a.y,
          scaleFactor * b.x,
          scaleFactor * b.y,
          paint
        )
      }
    }
  }
}