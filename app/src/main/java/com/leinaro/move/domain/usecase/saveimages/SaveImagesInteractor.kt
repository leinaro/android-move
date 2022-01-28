package com.leinaro.move.domain.usecase.saveimages

import android.graphics.Bitmap

interface SaveImagesInteractor {
  fun execute(uuid: String, bitmapList: List<Bitmap>)
}

