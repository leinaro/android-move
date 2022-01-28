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

package com.leinaro.move.presentation.capture;

import android.content.Intent;

/**
 * This class provides the constants to use when sending an Intent to Barcode Scanner.
 * These strings are effectively API and cannot be changed.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class Intents {
  private Intents() {
  }

  public static final class Scan {

    /**
     * By default, sending this will decode all barcodes that we understand. However it
     * may be useful to limit scanning to certain formats. Use
     * {@link Intent#putExtra(String, String)} with one of the values below.
     *
     * Setting this is effectively shorthand for setting explicit formats with {@link #FORMATS}.
     * It is overridden by that setting.
     */
    public static final String MODE = "SCAN_MODE";

    /**
     * Decode only UPC and EAN barcodes. This is the right choice for shopping apps which get
     * prices, reviews, etc. for products.
     */
    public static final String PRODUCT_MODE = "PRODUCT_MODE";

    /**
     * Decode only 1D barcodes.
     */
    public static final String ONE_D_MODE = "ONE_D_MODE";

    /**
     * Decode only QR codes.
     */
    public static final String QR_CODE_MODE = "QR_CODE_MODE";

    /**
     * Decode only Data Matrix codes.
     */
    public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

    /**
     * Decode only Aztec.
     */
    public static final String AZTEC_MODE = "AZTEC_MODE";

    /**
     * Decode only PDF417.
     */
    public static final String PDF417_MODE = "PDF417_MODE";

    /**
     * Comma-separated list of formats to scan for. The values must match the names of
     * {@link com.google.zxing.BarcodeFormat}s, e.g. {@link com.google.zxing.BarcodeFormat#EAN_13}.
     * Example: "EAN_13,EAN_8,QR_CODE". This overrides {@link #MODE}.
     */
    public static final String FORMATS = "SCAN_FORMATS";

    /**
     * @see com.google.zxing.DecodeHintType#CHARACTER_SET
     */
    public static final String CHARACTER_SET = "CHARACTER_SET";

    /**
     * Desired duration in milliseconds for which to pause after a successful scan before
     * returning to the calling intent. Specified as a long, not an integer!
     * For example: 1000L, not 1000.
     */
    public static final String RESULT_DISPLAY_DURATION_MS = "RESULT_DISPLAY_DURATION_MS";

    /**
     * Setting this to false will not save scanned codes in the history. Specified as a {@code boolean}.
     */
    public static final String SAVE_HISTORY = "SAVE_HISTORY";

    private Scan() {
    }
  }

  /**
   * Constants related to the scan history and retrieving history items.
   */
  public static final class History {

    public static final String ITEM_NUMBER = "ITEM_NUMBER";

    private History() {
    }
  }

  /**
   * Constants related to the {@link Encode#ACTION} Intent.
   */
  public static final class Encode {
    /**
     * Send this intent to encode a piece of data as a QR code and display it full screen, so
     * that another person can scan the barcode from your screen.
     */
    public static final String ACTION = "com.leinaro.move.presentation.capture.ENCODE";

    /**
     * The data to encode. Use {@link Intent#putExtra(String, String)} or
     * {@link Intent#putExtra(String, android.os.Bundle)},
     * depending on the type and format specified. Non-QR Code formats should
     * just use a String here. For QR Code, see Contents for details.
     */
    public static final String DATA = "ENCODE_DATA";

    /**
     * The type of data being supplied if the format is QR Code. Use
     * {@link Intent#putExtra(String, String)} with one of {@link Contents.Type}.
     */
    public static final String TYPE = "ENCODE_TYPE";

    /**
     * The barcode format to be displayed. If this isn't specified or is blank,
     * it defaults to QR Code. Use {@link Intent#putExtra(String, String)}, where
     * format is one of {@link com.google.zxing.BarcodeFormat}.
     */
    public static final String FORMAT = "ENCODE_FORMAT";

    /**
     * Normally the contents of the barcode are displayed to the user in a TextView. Setting this
     * boolean to false will hide that TextView, showing only the encode barcode.
     */
    public static final String SHOW_CONTENTS = "ENCODE_SHOW_CONTENTS";

    private Encode() {
    }
  }


  // Not the best place for this, but, better than a new class
  // Should be FLAG_ACTIVITY_NEW_DOCUMENT in API 21+.
  // Defined once here because the current value is deprecated, so generates just one warning
  @SuppressWarnings("deprecation")
  public static final int FLAG_NEW_DOC = Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
}
