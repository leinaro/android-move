package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap
import com.leinaro.move.domain.repository.LocalRepository
import com.leinaro.move.domain.data.BoxContent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveBoxDomainInteractor @Inject constructor(
  private val repository: LocalRepository,
) : SaveBoxInteractor {
  override fun execute(boxContent: BoxContent, bitmapList: List<Bitmap>): Flow<BoxContent> {
    return repository.saveBox(boxContent, bitmapList)
  }
}