package com.leinaro.move.domain.data

import com.leinaro.move.data.local.model.BoxEntity
import java.io.Serializable

data class BoxContent(
  val uuid: String,
  val counter: Int = -1,
  val location: String = "",
  val description: String = "",
  val photoPath: String = "",
  val inventoried: Boolean = false,
  val isNew: Boolean = false,
  val inventoryId: Long = -1,
  val id: Long? = null,
) : Serializable

fun BoxEntity?.toBoxContent(inventoried: Boolean = false): BoxContent? {
  return if (this == null) null
  else BoxContent(
    uuid = this.uuid.orEmpty(),
    counter = this.counter ?: 0,
    location = this.location.orEmpty(),
    description = this.description.orEmpty(),
    inventoried = inventoried,
    id = this.id,
    inventoryId = this.inventoryId
  )
}

fun BoxContent.toBoxEntity(): BoxEntity {
  return BoxEntity(
    uuid = this.uuid,
    location = this.location,
    description = this.description,
    counter = this.counter,
    id = this.id,
    inventoryId = this.inventoryId
  )
}