package com.leinaro.move.domain.usecase.getboxbyshortid

import com.leinaro.move.domain.data.BoxContent
import com.leinaro.move.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoxByShortIdDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetBoxByShortIdInteractor {
  override fun execute(shortId: String): Flow<BoxContent?> {
    return repository.getBoxByShortId(shortId)
  }
}

