package com.leinaro.move.domain.usecase.getinventory

import com.leinaro.move.domain.data.Inventory
import com.leinaro.move.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoryDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetInventoryInteractor {
  override fun execute(inventoryId: Long): Flow<Inventory?> {
    return repository.getInventory(inventoryId)
  }
}