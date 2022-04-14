package com.leinaro.move.domain.usecase.getinventorylist

import com.leinaro.move.domain.repository.LocalRepository
import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetInventoryListInteractor {
  fun execute(): Flow<List<Inventory>>
}

class GetInventoryListDomainInteractor @Inject constructor(
  private val repository: LocalRepository
) : GetInventoryListInteractor {
  override fun execute(): Flow<List<Inventory>> {
    return repository.getAllInventory()
  }
}
