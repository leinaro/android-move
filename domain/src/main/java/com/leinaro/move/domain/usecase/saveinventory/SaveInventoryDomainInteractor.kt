package com.leinaro.move.domain.usecase.saveinventory

import com.leinaro.move.domain.repository.LocalRepository
import com.leinaro.move.domain.repository.SharedPreferencesRepository
import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SaveInventoryDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
  private val sharedPreferencesRepository: SharedPreferencesRepository,
) : SaveInventoryInteractor {
  override fun execute(inventory: Inventory): Flow<Inventory> {
    return repository.saveInventory(inventory)
      .onEach {
        it.id?.let { inventoryId ->
          sharedPreferencesRepository.setInventoryId(inventoryId)
        }
      }
  }
}