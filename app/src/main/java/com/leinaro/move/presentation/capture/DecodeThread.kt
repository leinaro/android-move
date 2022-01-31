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

import android.os.Handler
import android.os.Looper
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.ResultPointCallback
import com.leinaro.move.presentation.capture.camera.CameraManager
import java.util.EnumMap
import java.util.concurrent.CountDownLatch

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
internal class DecodeThread(
  var cameraManager: CameraManager?,
  var handlerCamera: Handler?,
  var decodeFormats: Collection<BarcodeFormat>,
  resultPointCallback: ResultPointCallback?
) : Thread() {

  private val hints: MutableMap<DecodeHintType, Any?>
  private var handler: Handler? = null
  private val handlerInitLatch: CountDownLatch = CountDownLatch(1)

  init {
    hints = EnumMap(DecodeHintType::class.java)
    hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
    hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
  }

  fun getHandler(): Handler? {
    try {
      handlerInitLatch.await()
    } catch (ie: InterruptedException) {
      // continue?
    }
    return handler
  }

  override fun run() {
    Looper.prepare()
    handler = DecodeHandler(cameraManager, handlerCamera, hints)
    handlerInitLatch.countDown()
    Looper.loop()
  }

  companion object {
    const val BARCODE_BITMAP = "barcode_bitmap"
    const val BARCODE_SCALED_FACTOR = "barcode_scaled_factor"
  }
}