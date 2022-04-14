package com.leinaro.move.domain.usecase.getinvetories

import com.leinaro.move.domain.repository.LocalRepository
import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoriesDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetInventoriesInteractor {
  override fun execute(): Flow<List<Inventory>> {
    return repository.getInventories()
  }
}