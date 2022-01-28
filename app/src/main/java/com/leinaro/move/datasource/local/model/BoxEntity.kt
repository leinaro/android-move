package com.leinaro.move.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leinaro.move.BoxContent

@Entity
data class BoxEntity(
  @ColumnInfo(name = "location") val location: String?,
  @ColumnInfo(name = "uuid") val uuid: String?,
  @ColumnInfo(name = "description") val description: String?,
  @ColumnInfo(name = "counter") val counter: Int?,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

fun BoxEntity?.toBoxContent(): BoxContent? {
  return if (this == null) null
  else BoxContent(
    uuid = this.uuid.orEmpty(),
    counter = this.counter ?: 0,
    location = this.location.orEmpty(),
    description = this.description.orEmpty()
  )
}

fun BoxContent.toBoxEntity(): BoxEntity {
  return BoxEntity(
    uuid = this.uuid,
    location = this.location,
    description = this.description,
    counter = this.counter,
  )
}

