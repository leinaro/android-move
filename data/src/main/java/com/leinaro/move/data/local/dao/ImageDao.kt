package com.leinaro.move.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leinaro.move.data.local.model.ImageEntity

@Dao
interface ImageDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(vararg imageEntity: ImageEntity)

  @Query("SELECT * FROM ImageEntity") fun allImage(): List<ImageEntity>

  @Query("SELECT * FROM ImageEntity where boxUUID LIKE :boxUUID")
  fun getImageByImageId(boxUUID: String): List<ImageEntity>
}