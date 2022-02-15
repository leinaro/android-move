package com.leinaro.move.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InventoryEntity(
  @ColumnInfo(name = "boxUUID") val boxUUID: String,
  @ColumnInfo(name = "status") var status: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}