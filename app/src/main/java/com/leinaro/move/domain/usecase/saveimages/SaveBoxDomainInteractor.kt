package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap
import com.leinaro.move.presentation.data.BoxContent
import com.leinaro.move.domain.repository.LocalRepository
import javax.inject.Inject

class SaveBoxDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : SaveBoxInteractor {
  override fun execute(boxContent: BoxContent, bitmapList: List<Bitmap>) {
    repository.saveBox(boxContent, bitmapList)
  }
}