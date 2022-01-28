package com.leinaro.move.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leinaro.move.datasource.local.model.ImageEntity

@Dao
interface ImageDao {
  @Insert fun insert(vararg imageEntity: ImageEntity)

  @Query("SELECT * FROM ImageEntity") fun allImage(): List<ImageEntity>

  @Query("SELECT * FROM ImageEntity where boxUUID LIKE :boxUUID")
  fun getImageByImageId(boxUUID: String): List<ImageEntity>
}