package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap
import com.leinaro.move.domain.data.BoxContent
import kotlinx.coroutines.flow.Flow

interface SaveBoxInteractor {
  fun execute(boxContent: BoxContent, bitmapList: List<Bitmap>): Flow<BoxContent>
}

