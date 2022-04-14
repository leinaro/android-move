package com.leinaro.architecture_tools

import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val FORMAT_DD_MM_yyyy = "dd/MM/yyyy"
const val FORMAT_MMMM = "MMMM"
const val FORMAT_MMMM_yyyy = "MMMM yyyy"
const val FORMAT_MM_yyyy = "MM yyyy"
const val FORMAT_dd_MM_yyyy_hh_mm_ss = "dd/MM/yyyy hh:mm:ss"

fun Date.toFormatDate(format: String = FORMAT_MM_yyyy): String {
  val calendar = Calendar.getInstance()
  calendar.time = this
  val sf = SimpleDateFormat(format, Locale.getDefault())
  return sf.format(calendar.time)
}

fun String.toDate(format: String = FORMAT_MM_yyyy): Date? {
  val sf = SimpleDateFormat(format, Locale.getDefault())
  return sf.parse(this)
}

fun Double.toFormatNumber(locale: Locale): String? {
  val numberFormat = NumberFormat.getCurrencyInstance(locale)
  numberFormat.roundingMode = RoundingMode.HALF_UP
  numberFormat.maximumFractionDigits = 0
  numberFormat.minimumFractionDigits = 0
  return numberFormat.format(this)
}

