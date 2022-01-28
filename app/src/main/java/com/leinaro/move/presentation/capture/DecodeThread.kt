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
import java.util.EnumMap
import java.util.EnumSet
import java.util.concurrent.CountDownLatch

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
internal class DecodeThread(
  activity: CaptureActivity,
  decodeFormats: MutableCollection<BarcodeFormat?>?,
  baseHints: Map<DecodeHintType, *>?,
  characterSet: String?,
  resultPointCallback: ResultPointCallback?
) : Thread() {
  private val activity: CaptureActivity
  private val hints: MutableMap<DecodeHintType, Any?>
  private var handler: Handler? = null
  private val handlerInitLatch: CountDownLatch
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
    handler = DecodeHandler(activity, hints)
    handlerInitLatch.countDown()
    Looper.loop()
  }

  companion object {
    const val BARCODE_BITMAP = "barcode_bitmap"
    const val BARCODE_SCALED_FACTOR = "barcode_scaled_factor"
  }

  init {
    var decodeFormats = decodeFormats
    this.activity = activity
    handlerInitLatch = CountDownLatch(1)
    hints = EnumMap(DecodeHintType::class.java)
    if (baseHints != null) {
      hints.putAll(baseHints)
    }

    // The prefs can't change while the thread is running, so pick them up once here.
    decodeFormats?.addAll(EnumSet.allOf(BarcodeFormat::class.java))
    //if (decodeFormats == null || decodeFormats.isEmpty()) {
    /*val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
    decodeFormats = EnumSet.noneOf(BarcodeFormat::class.java)
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_PRODUCT, true)) {
      decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS)
    }
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_INDUSTRIAL, true)) {
      decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS)
    }
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
      decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
    }
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, true)) {
      decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
    }
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_AZTEC, false)) {
      decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS)
    }
    if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_PDF417, false)) {
      decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS)
    }*/
    // }
    hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
    if (characterSet != null) {
      hints[DecodeHintType.CHARACTER_SET] = characterSet
    }
    hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
  }
}

/*
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.ResultPointCallback
import java.util.EnumMap
import java.util.EnumSet
import java.util.concurrent.CountDownLatch

internal class DecodeThread(
  val activity: CaptureActivity,
//  var handlerCamera: Handler?,
//  var cameraManager: CameraManager?,
  // decodeFormats: Collection<BarcodeFormat>? = EnumSet.allOf(BarcodeFormat::class.java),
  baseHints: Map<DecodeHintType, *>?,
  characterSet: String?,
  resultPointCallback: ResultPointCallback?
) : Thread() {

  private val hints: MutableMap<DecodeHintType, Any?>
  private var decodeHandler: Handler? = null
  private val handlerInitLatch: CountDownLatch = CountDownLatch(1)

  init {
    Log.e("iarl", "init2")

    hints = EnumMap(DecodeHintType::class.java)
    baseHints?.let {
      hints.putAll(baseHints)
    }

    hints[DecodeHintType.POSSIBLE_FORMATS] = EnumSet.allOf(BarcodeFormat::class.java)//decodeFormats
    characterSet?.let {
      hints[DecodeHintType.CHARACTER_SET] = characterSet
    }
    hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
  }

  fun getHandler(): Handler? {
    try {
      handlerInitLatch.await()
    } catch (ie: InterruptedException) {
      // continue?
    }
    return decodeHandler
  }

  override fun run() {
    Log.e("iarl", "run")

    Looper.prepare()
    decodeHandler = DecodeHandler(
      activity, /*handlerCamera, cameraManager,*/
      hints
    )
    handlerInitLatch.countDown()
    Looper.loop()
  }

  companion object {
    const val BARCODE_BITMAP = "barcode_bitmap"
    const val BARCODE_SCALED_FACTOR = "barcode_scaled_factor"
  }
}*/