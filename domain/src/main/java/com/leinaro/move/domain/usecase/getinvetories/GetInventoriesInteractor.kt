package com.leinaro.move.domain.usecase.getinvetories

import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow

interface GetInventoriesInteractor {
  fun execute(): Flow<List<Inventory>>
}

