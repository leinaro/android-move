package com.leinaro.move.data.di

import com.leinaro.move.data.DataBaseClient
import com.leinaro.move.data.local.dao.BoxDao
import com.leinaro.move.data.local.dao.ImageDao
import com.leinaro.move.data.local.dao.InventoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  fun providesBoxDao(
    dataBase: DataBaseClient,
  ): BoxDao = dataBase.db.boxDao()

  @Provides
  fun providesImageDao(
    dataBase: DataBaseClient,
  ): ImageDao = dataBase.db.imageDao()

  @Provides
  fun providesInventoryDao(
    dataBase: DataBaseClient,
  ): InventoryDao = dataBase.db.inventoryDao()

}