package com.leinaro.move.domain.usecase.geimagesbyboxid

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface GetImagesByBoxIdInteractor {
  fun execute(uuid: String): Flow<List<Bitmap>>
}

