package com.leinaro.move.domain.usecase.geimagesbyboxid

import android.graphics.Bitmap
import com.leinaro.move.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImagesByBoxIdDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : GetImagesByBoxIdInteractor {
  override fun execute(uuid: String): Flow<List<Bitmap>> {
    return repository.getImagesByBoxId(uuid)
  }
}