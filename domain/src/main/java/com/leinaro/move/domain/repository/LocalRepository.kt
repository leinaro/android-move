package com.leinaro.move.domain.repository

import android.graphics.Bitmap
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
  fun getBoxByShortId(shortId: String): Flow<BoxContent?>
  fun getImagesByBoxId(uuid: String): Flow<List<Bitmap>>
  fun getAllBoxes(): Flow<List<BoxContent>>
  fun getAllBoxesWithInventoryStatus(): Flow<List<BoxContent>>
  fun saveBox(boxContent: BoxContent, bitmapList: List<Bitmap>?): Flow<BoxContent>
  fun saveImages(uuid: String, bitmapList: List<Bitmap>)
  fun updateBoxStatus(uuid: String)
  fun getAllInventory(): Flow<List<Inventory>>
  fun saveInventory(inventory: Inventory): Flow<Inventory>
  fun getInventory(inventoryId: Long): Flow<Inventory?>
  fun getInventories(): Flow<List<Inventory>>
  fun getBoxes(inventoryId: Long): Flow<List<BoxContent>>
}