package com.leinaro.move.domain.repository

import android.graphics.Bitmap
import com.leinaro.move.presentation.data.BoxContent
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
  fun getBoxByShortId(shortId: String): Flow<BoxContent?>
  fun getImagesByBoxId(uuid: String): Flow<List<Bitmap>>
  fun getAllBoxes(): Flow<List<BoxContent>>
  fun getAllBoxesWithInventoryStatus(): Flow<List<BoxContent>>
  fun saveBox(boxContent: BoxContent, bitmapList: List<Bitmap>?)
  fun saveImages(uuid: String, bitmapList: List<Bitmap>)
  fun updateBoxStatus(uuid: String)
}


