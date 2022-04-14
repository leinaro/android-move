package com.leinaro.move.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.leinaro.move.data.local.model.BoxEntity

@Dao
interface BoxDao {
  @Insert(onConflict = REPLACE) fun insert(vararg boxEntity: BoxEntity?): List<Long>

  @Query("SELECT * FROM BoxEntity") fun allBoxes(): List<BoxEntity>

  @Query("SELECT * FROM BoxEntity WHERE inventory_id = :inventoryId")
  fun getBoxes(inventoryId: Long): List<BoxEntity>

  @Query("SELECT * FROM BoxEntity WHERE uuid LIKE :uuid")
  fun getBoxByUUID(uuid: String): BoxEntity

  @Query("UPDATE BoxEntity SET inventory_id = :newInventoryId WHERE inventory_id = :inventoryId")
  fun updateInventoryId(inventoryId: Long, newInventoryId: Long): Int
}
