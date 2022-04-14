package com.leinaro.move.domain.usecase.updateboxstatus

import com.leinaro.move.domain.repository.LocalRepository
import javax.inject.Inject

class UpdateBoxStatusDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : UpdateBoxStatusInteractor {
  override fun execute(uuid: String) {
    return repository.updateBoxStatus(uuid)
  }
}