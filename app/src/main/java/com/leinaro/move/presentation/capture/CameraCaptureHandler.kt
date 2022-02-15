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

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.zxing.BarcodeFormat
import com.leinaro.move.R
import com.leinaro.move.presentation.capture.camera.CameraManager
import java.util.EnumSet

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
interface CameraCaptureListener {
  fun drawViewfinder()
  fun returnScanResult(intent: Intent)
  fun decodeSucceeded(message: Message)
  fun launchProductQuery(message: Message)
}

class CameraCaptureHandler internal constructor(
  private val listener: CameraCaptureListener,
  decodeFormats: Collection<BarcodeFormat> = EnumSet.allOf(BarcodeFormat::class.java),
  private val cameraManager: CameraManager,
) : Handler(Looper.getMainLooper()) {

  private val decodeThread: DecodeThread = DecodeThread(
    cameraManager,
    this,
    decodeFormats,
  )
  private var state: State = State.SUCCESS

  private enum class State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  init {
    decodeThread.start()

    // Start ourselves capturing previews and decoding.
    cameraManager.startPreview()
    restartPreviewAndDecode()
  }

  override fun handleMessage(message: Message) {
    when (message.what) {
      R.id.restart_preview -> restartPreviewAndDecode()
      R.id.decode_succeeded -> {
        state = State.SUCCESS
        listener.decodeSucceeded(message)
      }
      R.id.decode_failed -> {
        // We're decoding as fast as possible, so when one decode fails, start another.
        state = State.PREVIEW
        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode)
      }
      R.id.return_scan_result -> {
        listener.returnScanResult(message.obj as Intent)
      }
      R.id.launch_product_query -> {
        listener.launchProductQuery(message)
      }
      R.id.quit -> {
        //this.looper.quitSafely()
        quitSynchronously()
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
      listener.drawViewfinder()
    }
  }

  companion object {
    private val TAG = CameraCaptureHandler::class.java.simpleName
  }
}