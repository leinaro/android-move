package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap
import com.leinaro.move.domain.repository.LocalRepository
import javax.inject.Inject

class SaveImagesDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : SaveImagesInteractor {
  override fun execute(uuid: String, bitmapList: List<Bitmap>) {
    repository.saveImages(uuid, bitmapList)
  }
}