package com.leinaro.move.domain.repository

import android.graphics.Bitmap
import com.leinaro.move.BoxContent
import com.leinaro.move.datasource.DataBaseClient
import com.leinaro.move.datasource.local.ImageBitmapString
import com.leinaro.move.datasource.local.model.ImageEntity
import com.leinaro.move.datasource.local.model.toBoxContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(
  private val dataBase: DataBaseClient,
) : LocalRepository {
  override fun getBoxByShortId(shortId: String): Flow<BoxContent?> {
    return flow {
      emit(
        dataBase.db.boxDao().getBoxByUUID(shortId).toBoxContent()
      )
    }
  }

  override fun getImagesByBoxId(uuid: String): Flow<List<Bitmap>> {
    return flow {
      emit(
        dataBase.db.imageDao().getImageByImageId(uuid).mapNotNull {
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
      dataBase.db.imageDao().insert(*imageEntityList.toTypedArray())
    }
  }

  override fun getAllBoxes(): Flow<List<BoxContent>> {
    return flow {
      emit(
        dataBase.db.boxDao().allBoxes().mapNotNull {
          it.toBoxContent()
        }
      )
    }
  }
}