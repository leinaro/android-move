package com.leinaro.move.domain.usecase.getboxlist

import com.leinaro.move.BoxContent
import kotlinx.coroutines.flow.Flow

interface GetBoxListInteractor {
  fun execute(): Flow<List<BoxContent>>
}

