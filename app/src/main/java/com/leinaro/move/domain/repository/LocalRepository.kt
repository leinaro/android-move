package com.leinaro.move.domain.repository

import android.graphics.Bitmap
import com.leinaro.move.BoxContent
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
  fun getBoxByShortId(shortId: String): Flow<BoxContent?>
  fun getImagesByBoxId(uuid: String): Flow<List<Bitmap>>
  fun getAllBoxes(): Flow<List<BoxContent>>

  fun saveImages(uuid: String, bitmapList: List<Bitmap>)
  fun updateBoxStatus(uuid: String)
}


