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
import com.leinaro.move.presentation.capture.camera.CameraManager
import java.io.ByteArrayOutputStream

internal class DecodeHandler(
  private var cameraManager: CameraManager?,
  private var handlerCamera: Handler?,
  hints: Map<DecodeHintType, Any?>?
) : Handler() {
  private val multiFormatReader: MultiFormatReader = MultiFormatReader()
  private var running = true

  init {
    multiFormatReader.setHints(hints)
  }

  override fun handleMessage(message: Message) {
    if (!running) {
      return
    }
    when (message.what) {
      R.id.decode -> decode(message.obj as ByteArray, message.arg1, message.arg2)
      R.id.quit -> {
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
    val source = cameraManager?.buildLuminanceSource(data, width, height)
    var rawResult: Result? = null

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
    val handler: Handler? = handlerCamera
    if (rawResult != null) {
      // Don't log the barcode contents for security.
      if (handler != null) {
        val message = Message.obtain(handler, R.id.decode_succeeded, rawResult)
        val bundle = bundleThumbnail(source!!)
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
}