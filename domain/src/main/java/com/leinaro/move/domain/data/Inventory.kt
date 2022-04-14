package com.leinaro.move.domain.data

import com.leinaro.move.data.local.model.InventoryEntity
import java.io.Serializable

data class Inventory(
  val id: Long? = null,
  val origin: String,
  val destination: String,
) : Serializable

fun Inventory.toInventoryEntity(): InventoryEntity {
  return InventoryEntity(
    origin = this.origin,
    destination = this.destination,
    id = this.id,
  )
}

fun InventoryEntity.toInventory(): Inventory {
  return Inventory(
    origin = this.origin,
    destination = this.destination,
    id = this.id,
  )
}