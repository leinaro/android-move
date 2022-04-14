package com.leinaro.move.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PRIVATE_FILE_NAME = "move_private_file"

class SharedPreferencesClient @Inject constructor(
  @ApplicationContext applicationContext: Context
) {
  val sharedPrefences = applicationContext.getSharedPreferences(
    PRIVATE_FILE_NAME,
    Context.MODE_PRIVATE
  )
}