package com.leinaro.move.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BoxEntity(
  @ColumnInfo(name = "location") val location: String?,
  @ColumnInfo(name = "uuid") val uuid: String?,
  @ColumnInfo(name = "description") val description: String?,
  @ColumnInfo(name = "counter") val counter: Int?,
  @ColumnInfo(name = "inventory_id") val inventoryId: Long = -1,
  @PrimaryKey(autoGenerate = true) var id: Long? = null
)