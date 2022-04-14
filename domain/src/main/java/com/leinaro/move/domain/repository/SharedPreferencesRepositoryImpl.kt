package com.leinaro.move.domain.repository

import com.leinaro.move.data.SharedPreferencesClient
import javax.inject.Inject

private const val inventoryIdKey = "inventory_id_key"

class SharedPreferencesRepositoryImpl @Inject constructor(
  private val sharedPreferences: SharedPreferencesClient,
) : SharedPreferencesRepository {

  override fun getInventoryId(): Long {
    sharedPreferences.sharedPrefences.contains(inventoryIdKey)
    return try {
      sharedPreferences.sharedPrefences.getLong(inventoryIdKey, -1L)
    } catch (e: ClassCastException) {
      sharedPreferences.sharedPrefences.edit().remove(inventoryIdKey).apply()
      -1
    }
  }

  override fun setInventoryId(inventoryId: Long) {
    sharedPreferences.sharedPrefences.edit().putLong(inventoryIdKey, inventoryId).apply()
  }
}