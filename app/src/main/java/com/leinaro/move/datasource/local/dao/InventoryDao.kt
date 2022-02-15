package com.leinaro.move.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leinaro.move.datasource.local.model.InventoryEntity

@Dao
interface InventoryDao {
  @Insert fun insert(vararg inventoryEntity: InventoryEntity)

  @Query("SELECT * FROM InventoryEntity") fun getAllBoxStatus(): List<InventoryEntity>

  @Query("SELECT * FROM InventoryEntity where boxUUID LIKE :boxUUID")
  fun getBoxStatus(boxUUID: String): List<InventoryEntity>

  @Query("UPDATE InventoryEntity SET status=:status WHERE boxUUID = :boxUUID")
  fun updateBoxStatus(status: String, boxUUID: String)
}