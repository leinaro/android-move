package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap
import com.leinaro.move.presentation.data.BoxContent

interface SaveBoxInteractor {
  fun execute(boxContent: BoxContent, bitmapList: List<Bitmap>)
}

