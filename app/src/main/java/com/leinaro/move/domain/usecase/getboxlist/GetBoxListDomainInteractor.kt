package com.leinaro.move.domain.usecase.getboxlist

import com.leinaro.move.BoxContent
import com.leinaro.move.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoxListDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetBoxListInteractor {
  override fun execute(): Flow<List<BoxContent>> {
    return repository.getAllBoxes()
  }
}