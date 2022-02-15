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
package com.leinaro.move.presentation.capture.result

import android.app.Activity
import android.preference.PreferenceManager
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.leinaro.move.presentation.capture.PreferencesActivity

/**
 * A base class for the Android-specific barcode handlers. These allow the app to polymorphically
 * suggest the appropriate actions for each data type.
 *
 *
 * This class also contains a bunch of utility methods to take common actions like opening a URL.
 * They could easily be moved into a helper object, but it can't be static because the Activity
 * instance is needed to launch an intent.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
abstract class ResultHandler @JvmOverloads internal constructor(
  private val activity: Activity,
  val result: ParsedResult,
) {
  private val customProductSearch: String?

  /**
   * Some barcode contents are considered secure, and should not be saved to history, copied to the
   * clipboard, or otherwise persisted.
   *
   * @return If true, do not create any permanent record of these contents.
   */
  open fun areContentsSecure(): Boolean {
    return false
  }

  /**
   * Create a possibly styled string for the contents of the current barcode.
   *
   * @return The text to be displayed.
   */
  val displayContents: CharSequence
    get() {
      val contents = result.displayResult
      return contents.replace("\r", "")
    }

  /**
   * A string describing the kind of barcode that was found, e.g. "Found contact info".
   *
   * @return The resource ID of the string.
   */
  abstract val displayTitle: Int

  /**
   * A convenience method to get the parsed type. Should not be overridden.
   *
   * @return The parsed type, e.g. URI or ISBN
   */
  val type: ParsedResultType
    get() = result.type

  private fun parseCustomSearchURL(): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
    val customProductSearch = prefs.getString(
      PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH,
      null
    )
    return if (customProductSearch != null && customProductSearch.trim { it <= ' ' }.isEmpty()) {
      null
    } else customProductSearch
  }

  init {
    customProductSearch = parseCustomSearchURL()
  }
}