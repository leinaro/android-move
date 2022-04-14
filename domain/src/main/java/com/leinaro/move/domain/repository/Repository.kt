package com.leinaro.move.domain.repository

import android.graphics.Bitmap
import android.util.Log
import com.leinaro.move.data.local.ImageBitmapString
import com.leinaro.move.data.local.dao.BoxDao
import com.leinaro.move.data.local.dao.ImageDao
import com.leinaro.move.data.local.dao.InventoryDao
import com.leinaro.move.data.local.model.ImageEntity
import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.domain.data.toBoxContent
import com.leinaro.move.domain.data.toBoxEntity
import com.leinaro.move.domain.data.toInventory
import com.leinaro.move.domain.data.toInventoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(
  private val boxDao: BoxDao,
  private val imageDao: ImageDao,
  private val inventoryDao: InventoryDao,
) : LocalRepository {

  override fun getBoxByShortId(shortId: String): Flow<BoxContent?> {
    return flow {
      emit(
        boxDao.getBoxByUUID(shortId).toBoxContent(false)
      )
    }
  }

  override fun getImagesByBoxId(uuid: String): Flow<List<Bitmap>> {
    return flow {
      emit(
        imageDao.getImageByImageId(uuid).mapNotNull {
          ImageBitmapString().stringToBitMap(it.image)
        }
      )
    }
  }

  override fun saveImages(uuid: String, bitmapList: List<Bitmap>) {
    val imageEntityList = bitmapList.mapNotNull {
      ImageBitmapString().bitMapToString(it)
    }.map { bitmapString ->
      ImageEntity(boxUUID = uuid, image = bitmapString)
    }
    if (imageEntityList.isNotEmpty()) {
      imageDao.insert(*imageEntityList.toTypedArray())
    }
  }

  override fun saveBox(boxContent: BoxContent, bitmapList: List<Bitmap>?): Flow<BoxContent> {
    return flow {
      val boxEntity = boxContent.toBoxEntity()
      val id = boxDao.insert(boxEntity).first()
      emit(boxContent.copy(id = id))
      //inventoryDao.insert(InventoryEntity(boxContent.uuid, "IN_MOVE"))
      bitmapList?.let {
        saveImages(boxContent.uuid, it)
      }
    }
  }

  override fun saveInventory(inventory: Inventory): Flow<Inventory> {
    return flow {
      val inventoryEntity = inventory.toInventoryEntity()
      val id = inventoryDao.insert(inventoryEntity).first()
      val count = boxDao.updateInventoryId(-1, id)
      Log.e("iarl", "count $count")

      emit(inventory.copy(id = id))
    }
  }

  override fun getInventory(inventoryId: Long): Flow<Inventory?> {
    return flow {
      emit(inventoryDao.getInventory(inventoryId)?.toInventory())
    }
  }

  override fun getInventories(): Flow<List<Inventory>> {
    return flow {
      emit(inventoryDao.getAllInventories().map { it.toInventory() })
    }
  }

  override fun getAllBoxes(): Flow<List<BoxContent>> {
    return flow {
      emit(
        boxDao.allBoxes().mapNotNull { box ->
          box.toBoxContent()
        }
      )
    }
  }

  override fun getBoxes(inventoryId: Long): Flow<List<BoxContent>> {
    return flow {
      emit(
        boxDao.getBoxes(inventoryId).mapNotNull { box ->
          box.toBoxContent()
        }
      )
    }
  }

  override fun getAllBoxesWithInventoryStatus(): Flow<List<BoxContent>> {
    return flow {
      val status = inventoryDao.getAllInventories()
      /*emit(
        boxDao.allBoxes().mapNotNull { box ->
          val s = status.firstOrNull { inventory ->
            inventory.id == box.uuid
          }?.status == "ARRIVED"
          box.toBoxContent(s)
        }.sortedBy { it.inventoried }
      )*/
    }
  }

  override fun updateBoxStatus(uuid: String) {
    //val status = if (boxContent.inventoried) "ARRIVED" else "IN_MOVE"
    //boxDao.getBoxByUUID(uuid)
    // inventoryDao.updateBoxStatus(boxUUID = uuid, status = "ARRIVED")
    //flow {
    //boxDao.insert(boxEntity)
    /*emit(
      boxDao.allBoxes().mapNotNull {
        it.toBoxContent()
      }
    )*/
    //}
  }

  override fun getAllInventory(): Flow<List<Inventory>> {
    return flow {
      emit(inventoryDao.getAllInventories().map { it.toInventory() })
    }
  }
}