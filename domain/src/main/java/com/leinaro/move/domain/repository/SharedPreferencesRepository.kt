package com.leinaro.move.domain.repository

interface SharedPreferencesRepository {
  fun getInventoryId(): Long
  fun setInventoryId(inventoryId: Long)
}