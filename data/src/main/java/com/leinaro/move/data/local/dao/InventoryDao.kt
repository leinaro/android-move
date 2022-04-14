package com.leinaro.move.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leinaro.move.data.local.model.BoxEntity
import com.leinaro.move.data.local.model.InventoryEntity

@Dao
interface InventoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg inventoryEntity: InventoryEntity): List<Long>

  @Query("SELECT * FROM InventoryEntity")
  fun getAllInventories(): List<InventoryEntity>

  @Query("SELECT * FROM InventoryEntity WHERE id = :inventoryId")
  fun getInventory(inventoryId: Long): InventoryEntity?
}

@Dao
interface InventoryBoxDao {
  @Query(
    """SELECT * FROM inventoryentity 
      JOIN boxentity ON inventoryentity.id = boxentity.inventory_id"""
  )
  fun getBoxesByInventory(): Map<InventoryEntity, List<BoxEntity>>
}