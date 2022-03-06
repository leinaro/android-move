package com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus

import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoxListWithInventoryStatusDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetBoxListWithInventoryStatusInteractor {
  override fun execute(): Flow<List<BoxContent>> {
    return repository.getAllBoxesWithInventoryStatus()
  }
}