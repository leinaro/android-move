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

/**
 * This class provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
object Intents {
  // Not the best place for this, but, better than a new class
  // Should be FLAG_ACTIVITY_NEW_DOCUMENT in API 21+.
  // Defined once here because the current value is deprecated, so generates just one warning
  const val FLAG_NEW_DOC = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET

  object Scan {
    /**
     * Setting this to false will not save scanned codes in the history. Specified as a `boolean`.
     */
    const val SAVE_HISTORY = "SAVE_HISTORY"
  }

  /**
   * Constants related to the scan history and retrieving history items.
   */
  object History {
    const val ITEM_NUMBER = "ITEM_NUMBER"
  }
}