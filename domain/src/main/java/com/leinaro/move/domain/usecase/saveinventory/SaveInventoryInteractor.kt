package com.leinaro.move.domain.usecase.saveinventory

import com.leinaro.move.domain.data.Inventory
import kotlinx.coroutines.flow.Flow

interface SaveInventoryInteractor {
  fun execute(inventory: Inventory): Flow<Inventory>
}

