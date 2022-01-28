package com.leinaro.move.domain.usecase.getboxbyshortid

import com.leinaro.move.BoxContent
import kotlinx.coroutines.flow.Flow

interface GetBoxByShortIdInteractor {
  fun execute(shortId: String): Flow<BoxContent?>
}

