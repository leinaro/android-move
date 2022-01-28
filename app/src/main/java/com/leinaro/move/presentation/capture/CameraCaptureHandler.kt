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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Browser
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import com.leinaro.move.R
import com.leinaro.move.presentation.capture.camera.CameraManager

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
class CameraCaptureHandler internal constructor(
  private val activity: CaptureActivity,
  decodeFormats: MutableCollection<BarcodeFormat?>?,
  baseHints: Map<DecodeHintType, *>?,
  characterSet: String?,
  cameraManager: CameraManager
) : Handler() {
  private val decodeThread: DecodeThread
  private var state: State
  private val cameraManager: CameraManager

  private enum class State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  override fun handleMessage(message: Message) {
    when (message.what) {
      R.id.restart_preview -> restartPreviewAndDecode()
      R.id.decode_succeeded -> {
        state = State.SUCCESS
        val bundle = message.data
        var barcode: Bitmap? = null
        var scaleFactor = 1.0f
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
        activity.handleDecode((message.obj as Result), barcode, scaleFactor)
      }
      R.id.decode_failed -> {
        // We're decoding as fast as possible, so when one decode fails, start another.
        state = State.PREVIEW
        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
      }
      R.id.return_scan_result -> {
        activity.setResult(Activity.RESULT_OK, message.obj as Intent)
        activity.finish()
      }
      R.id.launch_product_query -> {
        val url = message.obj as String
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intents.FLAG_NEW_DOC)
        intent.data = Uri.parse(url)
        val resolveInfo =
          activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
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
          activity.startActivity(intent)
        } catch (ignored: ActivityNotFoundException) {
          Log.w(TAG, "Can't find anything to handle VIEW of URI")
        }
      }
    }
  }

  fun quitSynchronously() {
    state = State.DONE
    cameraManager.stopPreview()
    val quit: Message = Message.obtain(decodeThread.getHandler(), R.id.quit)
    quit.sendToTarget()
    try {
      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
      decodeThread.join(500L)
    } catch (e: InterruptedException) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded)
    removeMessages(R.id.decode_failed)
  }

  private fun restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW
      cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
      activity.drawViewfinder()
    }
  }

  companion object {
    private val TAG = CameraCaptureHandler::class.java.simpleName
  }

  init {
    decodeThread = DecodeThread(
      activity, decodeFormats, baseHints, characterSet,
      ViewfinderResultPointCallback(activity.binding.viewfinderView)
    )
    decodeThread.start()
    state = State.SUCCESS

    // Start ourselves capturing previews and decoding.
    this.cameraManager = cameraManager
    cameraManager.startPreview()
    restartPreviewAndDecode()
  }
}

/*
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import com.leinaro.move.R
import com.leinaro.move.presentation.capture.camera.CameraManager

interface CameraCaptureListener {
  fun handleDecode(result: Result, bitmap: Bitmap?, scaleFactor: Float)
  fun returnScanResult(resultCode: Int, intent: Intent)
  fun launchProductQuery(url: String)
  fun restartPreviewAndDecode(handler: Handler?)
}

class CameraCaptureHandler internal constructor(
  private val listener: CameraCaptureListener?,
  val activity: CaptureActivity,
 // decodeFormats: Collection<BarcodeFormat>,
  baseHints: Map<DecodeHintType, *>?,
  characterSet: String?,
//  private var decodeThread: DecodeThread,
  private val cameraManager: CameraManager
) : Handler(Looper.getMainLooper()) {

  private var decodeThread: DecodeThread = DecodeThread(
    activity,
    //activity.handlerCamera, activity.cameraManager,
   // decodeFormats,
    baseHints, characterSet,
    ViewfinderResultPointCallback(activity.binding.viewfinderView)
  )
  private var state: State = State.SUCCESS

  enum class State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  init {
    Log.e("iarl", "init ")

    decodeThread.start()

    // Start ourselves capturing previews and decoding.
    cameraManager.startPreview()
    restartPreviewAndDecode()
  }

  private fun decodeSuccess(message: Message) {
    Log.e("iarl", "decodeSuccess ${message.what}")
    state = State.SUCCESS
    val bundle = message.data
    var bitmap: Bitmap? = null
    var scaleFactor = 1.0f
    if (bundle != null) {
      val compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP)
      if (compressedBitmap != null) {
        bitmap = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.size, null)
        // Mutable copy:
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
      }
      scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR)
    }
    listener?.handleDecode(
      (message.obj as Result),
      bitmap,
      scaleFactor
    )
  }

  override fun handleMessage(message: Message) {
    Log.e("iarl", "handleMessage ${message.what}")
    when (message.what) {
      R.id.restart_preview -> restartPreviewAndDecode()
      R.id.decode_succeeded -> decodeSuccess(message)
      R.id.decode_failed -> {
        // We're decoding as fast as possible, so when one decode fails, start another.
        state = State.PREVIEW
        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
      }
      R.id.return_scan_result -> {
        listener?.returnScanResult(Activity.RESULT_OK, message.obj as Intent)
      }
      R.id.launch_product_query -> {
        val url = message.obj as String
        listener?.launchProductQuery(url)
      }
    }
  }

  fun quitSynchronously() {
    Log.e("iarl", "quitSynchronously")

    state = State.DONE
    cameraManager.stopPreview()
    val quit = Message.obtain(decodeThread.getHandler(), R.id.quit)
    quit.sendToTarget()
    try {
      // Wait at most half a second; should be enough time, and onPause() will timeout quickly
      decodeThread.join(500L)
    } catch (e: InterruptedException) {
      // continue
    }

    // Be absolutely sure we don't send any queued up messages
    removeMessages(R.id.decode_succeeded)
    removeMessages(R.id.decode_failed)
  }

  private fun restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      state = State.PREVIEW
      listener?.restartPreviewAndDecode(decodeThread.getHandler())
    }
  }

  companion object {
    private val TAG = CameraCaptureHandler::class.java.simpleName
  }
}*/