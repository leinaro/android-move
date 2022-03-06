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

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.Browser
import android.util.Log
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.leinaro.move.presentation.boxdetails.BoxDetailsActivity
import com.leinaro.move.R
import com.leinaro.move.databinding.ActivityCaptureBinding
import com.leinaro.move.databinding.ActivityMainBinding
import com.leinaro.move.presentation.capture.camera.CameraManager
import com.leinaro.move.presentation.capture.result.ResultHandlerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.EnumSet

@AndroidEntryPoint
class CaptureActivity : AppCompatActivity(), SurfaceHolder.Callback, CameraCaptureListener {

  private lateinit var binding: ActivityCaptureBinding

//  private val binding by viewBinding(ActivityCaptureBinding::inflate)

  private val viewModel: CaptureViewModel by viewModels()

  private var cameraManager: CameraManager? = null
  private var handlerCamera: CameraCaptureHandler? = null

  private var savedResultToShow: Result? = null
  private var lastResult: Result? = null

  private var hasSurface = false

  private var inactivityTimer: InactivityTimer? = null
  private var beepManager: BeepManager? = null
  private var ambientLightManager: AmbientLightManager? = null

  public override fun onCreate(icicle: Bundle?) {
    super.onCreate(icicle)

    val window = window
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    binding = ActivityCaptureBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
    //setContentView(binding.root)

    initView()

    setObserver()
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
    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    setUpCameraManager()

    handlerCamera = null
    lastResult = null

    resetStatusView()

    ambientLightManager?.start(cameraManager)
    inactivityTimer?.onResume()

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

  private fun setUpCameraManager() {
    cameraManager = CameraManager(this)
  }

  override fun onPause() {
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
  private fun handleDecode(rawResult: Result, bitmap: Bitmap?, scaleFactor: Float) {
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
    beepManager?.playBeepSoundAndVibrate()

    viewModel.handleDecodeInternally(rawResult, resultHandler)
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
    lastResult = null
  }

  override fun drawViewfinder() {
    //binding.viewfinderView.drawViewfinder()
  }

  override fun returnScanResult(intent: Intent) {
    setResult(Activity.RESULT_OK, intent)
    finish()
  }

  override fun decodeSucceeded(message: Message) {
    var barcode: Bitmap? = null
    var scaleFactor = 1.0f
    val bundle = message.data
    if (bundle != null) {
      val compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP)
      if (compressedBitmap != null) {
        barcode =
          BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.size, null)
        // Mutable copy:
        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true)
      }
      scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR)
    }
    handleDecode((message.obj as Result), barcode, scaleFactor)
  }

  override fun launchProductQuery(message: Message) {
    val url = message.obj as String

    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intents.FLAG_NEW_DOC)
    intent.data = Uri.parse(url)

    val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    var browserPackageName: String? = null
    if (resolveInfo != null && resolveInfo.activityInfo != null) {
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
      startActivity(intent)
    } catch (ignored: ActivityNotFoundException) {
      Log.w(TAG, "Can't find anything to handle VIEW of URI")
    }
  }

    private val TAG = CaptureActivity::class.java.simpleName
}