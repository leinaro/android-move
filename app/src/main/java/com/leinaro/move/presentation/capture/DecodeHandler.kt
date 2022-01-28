/*
 * Copyright (C) 2010 ZXing authors
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

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.leinaro.move.R
import java.io.ByteArrayOutputStream

internal class DecodeHandler(activity: CaptureActivity, hints: MutableMap<DecodeHintType, Any?>?) :
  Handler() {
  private val activity: CaptureActivity
  private val multiFormatReader: MultiFormatReader
  private var running = true
  override fun handleMessage(message: Message) {
    if (message == null || !running) {
      return
    }
    when (message.what) {
      R.id.decode -> decode(message.obj as ByteArray, message.arg1, message.arg2)
      R.id.quit -> {
        running = false
        Looper.myLooper()!!.quit()
      }
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private fun decode(data: ByteArray, width: Int, height: Int) {
    var rawResult: Result? = null
    val source: PlanarYUVLuminanceSource? =
      activity.cameraManager?.buildLuminanceSource(data, width, height)
    if (source != null) {
      val bitmap = BinaryBitmap(HybridBinarizer(source))
      try {
        rawResult = multiFormatReader.decodeWithState(bitmap)
      } catch (re: ReaderException) {
        // continue
      } finally {
        multiFormatReader.reset()
      }
    }
    val handler: Handler = activity.handlerCamera!!
    if (rawResult != null) {
      // Don't log the barcode contents for security.
      if (handler != null) {
        val message = Message.obtain(handler, R.id.decode_succeeded, rawResult)
        val bundle = Bundle()
        bundleThumbnail(source, bundle)
        message.data = bundle
        message.sendToTarget()
      }
    } else {
      if (handler != null) {
        val message: Message = Message.obtain(handler, R.id.decode_failed)
        message.sendToTarget()
      }
    }
  }

  companion object {
    private fun bundleThumbnail(source: PlanarYUVLuminanceSource?, bundle: Bundle) {
      val pixels = source!!.renderThumbnail()
      val width = source.thumbnailWidth
      val height = source.thumbnailHeight
      val bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888)
      val out = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
      bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray())
      bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, width.toFloat() / source.width)
    }
  }

  init {
    multiFormatReader = MultiFormatReader()
    multiFormatReader.setHints(hints)
    this.activity = activity
  }
}
/*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.leinaro.move.R
import com.leinaro.move.presentation.capture.camera.CameraManager
import java.io.ByteArrayOutputStream

internal class DecodeHandler(
  val activity: CaptureActivity,
  //var handler: Handler?,
  //var cameraManager: CameraManager?,
  hints: MutableMap<DecodeHintType, Any?>?
) : Handler() {

  private val multiFormatReader: MultiFormatReader = MultiFormatReader()
  private var running = true

  val handler: Handler? = activity.handlerCamera

  init {
    multiFormatReader.setHints(hints)
  }

  override fun handleMessage(message: Message) {
    Log.e("iarl", "handleMessage2 ${message.what}")

    if (!running) {
      Log.e("iarl", "is not running")

      return
    }

    when (message.what) {
      R.id.decode -> decode(message.obj as ByteArray, message.arg1, message.arg2)
      R.id.quit -> {
        Log.e("iarl", "quit")
        running = false
        Looper.myLooper()?.quit()
      }
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private fun decode(data: ByteArray, width: Int, height: Int) {
    Log.e("iarl", "decode2")

    val source = activity.cameraManager?.buildLuminanceSource(data, width, height)

    var rawResult: Result? = null

    source?.let {
      Log.e("iarl", "source ")
      val bitmap = BinaryBitmap(HybridBinarizer(source))
      try {
        rawResult = multiFormatReader.decodeWithState(bitmap)
      } catch (re: ReaderException) {
        // continue
      } finally {
        multiFormatReader.reset()
      }
    }

    rawResult?.let {
      Log.e("iarl", "rawResult ${rawResult}")

      // Don't log the barcode contents for security.
      if (handler != null) {
        Log.e("iarl", "handler is not null")

        val message = Message.obtain(handler, R.id.decode_succeeded, rawResult)
        val bundle = bundleThumbnail(source!!)
        message.data = bundle
        message.sendToTarget()
      }
    } ?: run {
      Log.e("iarl", "rawResult is null")

      if (handler != null) {
        Log.e("iarl", "handler is not null")

        val message = Message.obtain(handler, R.id.decode_failed)
        message.sendToTarget()
      }
    }
  }
}

private fun bundleThumbnail(source: PlanarYUVLuminanceSource): Bundle {
  val pixels = source.renderThumbnail()
  val width = source.thumbnailWidth
  val height = source.thumbnailHeight
  val out = createBitmap(pixels, 0, width, height)
  val bundle = Bundle()
  bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray())
  bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, width.toFloat() / source.width)
  return bundle
}

private fun createBitmap(
  pixels: IntArray, offSet: Int, width: Int, height: Int,
): ByteArrayOutputStream {
  val bitmap = Bitmap.createBitmap(pixels, offSet, width, width, height, Bitmap.Config.ARGB_8888)
  val out = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
  return out
}*/