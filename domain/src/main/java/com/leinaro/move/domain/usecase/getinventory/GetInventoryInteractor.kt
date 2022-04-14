package com.leinaro.move.domain.usecase.getinventory

import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow

interface GetInventoryInteractor {
  fun execute(inventoryId: Long): Flow<Inventory?>
}