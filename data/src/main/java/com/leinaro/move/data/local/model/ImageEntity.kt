package com.leinaro.move.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageEntity(
  @ColumnInfo(name = "boxUUID") val boxUUID: String,
  @ColumnInfo(name = "image") var image: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}