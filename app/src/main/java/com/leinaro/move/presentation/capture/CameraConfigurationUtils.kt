/*
 * Copyright (C) 2014 ZXing authors
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

import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.util.Log
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.Arrays

/**
 * Utility methods for configuring the Android camera.
 *
 * @author Sean Owen
 */
// camera APIs
object CameraConfigurationUtils {
  private const val TAG = "CameraConfiguration"
  private const val MIN_PREVIEW_PIXELS = 480 * 320 // normal screen
  private const val MAX_ASPECT_DISTORTION = 0.15

  private const val AREA_PER_1000 = 400

  fun setFocus(
    parameters: Camera.Parameters,
    autoFocus: Boolean,
    disableContinuous: Boolean,
    safeMode: Boolean
  ) {
    val supportedFocusModes = parameters.supportedFocusModes
    var focusMode: String? = null
    if (autoFocus) {
      focusMode = if (safeMode || disableContinuous) {
        findSettableValue(
          "focus mode",
          supportedFocusModes,
          Camera.Parameters.FOCUS_MODE_AUTO
        )
      } else {
        findSettableValue(
          "focus mode",
          supportedFocusModes,
          Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
          Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
          Camera.Parameters.FOCUS_MODE_AUTO
        )
      }
    }
    // Maybe selected auto-focus but not available, so fall through here:
    if (!safeMode && focusMode == null) {
      focusMode = findSettableValue(
        "focus mode",
        supportedFocusModes,
        Camera.Parameters.FOCUS_MODE_MACRO,
        Camera.Parameters.FOCUS_MODE_EDOF
      )
    }
    if (focusMode != null) {
      if (focusMode == parameters.focusMode) {
        Log.i(TAG, "Focus mode already set to $focusMode")
      } else {
        parameters.focusMode = focusMode
      }
    }
  }

  fun setTorch(parameters: Camera.Parameters, on: Boolean) {
    val supportedFlashModes = parameters.supportedFlashModes
    val flashMode: String? = if (on) {
      findSettableValue(
        "flash mode",
        supportedFlashModes,
        Camera.Parameters.FLASH_MODE_TORCH,
        Camera.Parameters.FLASH_MODE_ON
      )
    } else {
      findSettableValue(
        "flash mode",
        supportedFlashModes,
        Camera.Parameters.FLASH_MODE_OFF
      )
    }
    if (flashMode != null) {
      if (flashMode == parameters.flashMode) {
        Log.i(TAG, "Flash mode already set to $flashMode")
      } else {
        Log.i(TAG, "Setting flash mode to $flashMode")
        parameters.flashMode = flashMode
      }
    }
  }

  fun setFocusArea(parameters: Camera.Parameters) {
    if (parameters.maxNumFocusAreas > 0) {
      Log.i(TAG, "Old focus areas: " + toString(parameters.focusAreas))
      val middleArea = buildMiddleArea()
      Log.i(TAG, "Setting focus area to : " + toString(middleArea))
      parameters.focusAreas = middleArea
    } else {
      Log.i(TAG, "Device does not support focus areas")
    }
  }

  fun setMetering(parameters: Camera.Parameters) {
    if (parameters.maxNumMeteringAreas > 0) {
      Log.i(TAG, "Old metering areas: " + parameters.meteringAreas)
      val middleArea = buildMiddleArea()
      Log.i(TAG, "Setting metering area to : " + toString(middleArea))
      parameters.meteringAreas = middleArea
    } else {
      Log.i(TAG, "Device does not support metering areas")
    }
  }

  private fun buildMiddleArea(): List<Camera.Area> {
    return listOf(
      Camera.Area(Rect(-AREA_PER_1000, -AREA_PER_1000, AREA_PER_1000, AREA_PER_1000), 1)
    )
  }

  fun setVideoStabilization(parameters: Camera.Parameters) {
    if (parameters.isVideoStabilizationSupported) {
      if (parameters.videoStabilization) {
        Log.i(TAG, "Video stabilization already enabled")
      } else {
        Log.i(TAG, "Enabling video stabilization...")
        parameters.videoStabilization = true
      }
    } else {
      Log.i(TAG, "This device does not support video stabilization")
    }
  }

  fun setBarcodeSceneMode(parameters: Camera.Parameters) {
    if (Camera.Parameters.SCENE_MODE_BARCODE == parameters.sceneMode) {
      Log.i(TAG, "Barcode scene mode already set")
      return
    }
    val sceneMode = findSettableValue(
      "scene mode",
      parameters.supportedSceneModes,
      Camera.Parameters.SCENE_MODE_BARCODE
    )
    if (sceneMode != null) {
      parameters.sceneMode = sceneMode
    }
  }

  fun findBestPreviewSizeValue(parameters: Camera.Parameters, screenResolution: Point): Point {
    val rawSupportedSizes = parameters.supportedPreviewSizes
    if (rawSupportedSizes == null) {
      Log.w(TAG, "Device returned no supported preview sizes; using default")
      val defaultSize = parameters.previewSize
        ?: throw IllegalStateException("Parameters contained no preview size!")
      return Point(defaultSize.width, defaultSize.height)
    }
    if (Log.isLoggable(TAG, Log.INFO)) {
      val previewSizesString = StringBuilder()
      for (size in rawSupportedSizes) {
        previewSizesString.append(size.width).append('x').append(size.height).append(' ')
      }
      Log.i(TAG, "Supported preview sizes: $previewSizesString")
    }
    val screenAspectRatio = screenResolution.x / screenResolution.y.toDouble()

    // Find a suitable size, with max resolution
    var maxResolution = 0
    var maxResPreviewSize: Camera.Size? = null
    for (size in rawSupportedSizes) {
      val realWidth = size.width
      val realHeight = size.height
      val resolution = realWidth * realHeight
      if (resolution < MIN_PREVIEW_PIXELS) {
        continue
      }
      val isCandidatePortrait = realWidth < realHeight
      val maybeFlippedWidth = if (isCandidatePortrait) realHeight else realWidth
      val maybeFlippedHeight = if (isCandidatePortrait) realWidth else realHeight
      val aspectRatio = maybeFlippedWidth / maybeFlippedHeight.toDouble()
      val distortion = Math.abs(aspectRatio - screenAspectRatio)
      if (distortion > MAX_ASPECT_DISTORTION) {
        continue
      }
      if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
        val exactPoint = Point(realWidth, realHeight)
        Log.i(TAG, "Found preview size exactly matching screen size: $exactPoint")
        return exactPoint
      }

      // Resolution is suitable; record the one with max resolution
      if (resolution > maxResolution) {
        maxResolution = resolution
        maxResPreviewSize = size
      }
    }

    // If no exact match, use largest preview size. This was not a great idea on older devices because
    // of the additional computation needed. We're likely to get here on newer Android 4+ devices, where
    // the CPU is much more powerful.
    if (maxResPreviewSize != null) {
      val largestSize = Point(maxResPreviewSize.width, maxResPreviewSize.height)
      Log.i(TAG, "Using largest suitable preview size: $largestSize")
      return largestSize
    }

    // If there is nothing at all suitable, return current preview size
    val defaultPreview = parameters.previewSize
      ?: throw IllegalStateException("Parameters contained no preview size!")
    val defaultSize = Point(defaultPreview.width, defaultPreview.height)
    Log.i(TAG, "No suitable preview sizes, using default: $defaultSize")
    return defaultSize
  }

  private fun findSettableValue(
    name: String,
    supportedValues: Collection<String>?,
    vararg desiredValues: String
  ): String? {
    Log.i(TAG, "Requesting " + name + " value from among: " + Arrays.toString(desiredValues))
    Log.i(TAG, "Supported $name values: $supportedValues")
    if (supportedValues != null) {
      for (desiredValue in desiredValues) {
        if (supportedValues.contains(desiredValue)) {
          Log.i(TAG, "Can set $name to: $desiredValue")
          return desiredValue
        }
      }
    }
    Log.i(TAG, "No supported values match")
    return null
  }

  private fun toString(areas: Iterable<Camera.Area>?): String? {
    if (areas == null) {
      return null
    }
    val result = StringBuilder()
    for (area in areas) {
      result.append(area.rect).append(':').append(area.weight).append(' ')
    }
    return result.toString()
  }
}