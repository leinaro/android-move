package com.leinaro.move.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.leinaro.move.datasource.local.model.BoxEntity

@Dao
interface BoxDao {
  @Insert(onConflict = REPLACE) fun insert(vararg boxEntity: BoxEntity?)

  @Query("SELECT * FROM BoxEntity") fun allBoxes(): List<BoxEntity>

  @Query("SELECT * FROM BoxEntity WHERE uuid LIKE :uuid")
  fun getBoxByUUID(uuid: String): BoxEntity
}
