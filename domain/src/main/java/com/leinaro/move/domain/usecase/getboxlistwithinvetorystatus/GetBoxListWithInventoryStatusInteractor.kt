package com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus

import com.leinaro.move.domain.data.BoxContent
import kotlinx.coroutines.flow.Flow

interface GetBoxListWithInventoryStatusInteractor {
  fun execute(): Flow<List<BoxContent>>
}