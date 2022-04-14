package com.leinaro.move.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InventoryEntity(
  @ColumnInfo(name = "origin") var origin: String,
  @ColumnInfo(name = "destination") var destination: String,
  @PrimaryKey(autoGenerate = true) var id: Long? = null
)