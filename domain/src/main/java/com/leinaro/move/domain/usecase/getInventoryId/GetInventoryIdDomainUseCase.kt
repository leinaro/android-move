package com.leinaro.move.domain.usecase.getInventoryId

import com.leinaro.move.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

class GetInventoryIdDomainUseCase @Inject constructor(
  private val repository: SharedPreferencesRepository
) : GetInventoryIdUseCase {
  override fun execute(): Long {
    return repository.getInventoryId()
  }
}