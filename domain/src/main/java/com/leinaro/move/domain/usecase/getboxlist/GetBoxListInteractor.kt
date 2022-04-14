package com.leinaro.move.domain.usecase.getboxlist

import com.leinaro.move.domain.data.BoxContent
import kotlinx.coroutines.flow.Flow

interface GetBoxListInteractor {
  fun execute(inventoryId: Long): Flow<List<BoxContent>>
}

