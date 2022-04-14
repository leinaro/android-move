package com.leinaro.move.domain.usecase.di

import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdDomainInteractor
import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdInteractor
import com.leinaro.move.domain.usecase.getInventoryId.GetInventoryIdDomainUseCase
import com.leinaro.move.domain.usecase.getInventoryId.GetInventoryIdUseCase
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdDomainInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdInteractor
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListDomainInteractor
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListInteractor
import com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus.GetBoxListWithInventoryStatusDomainInteractor
import com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus.GetBoxListWithInventoryStatusInteractor
import com.leinaro.move.domain.usecase.getinventory.GetInventoryDomainInteractor
import com.leinaro.move.domain.usecase.getinventory.GetInventoryInteractor
import com.leinaro.move.domain.usecase.getinventorylist.GetInventoryListDomainInteractor
import com.leinaro.move.domain.usecase.getinventorylist.GetInventoryListInteractor
import com.leinaro.move.domain.usecase.getinvetories.GetInventoriesDomainInteractor
import com.leinaro.move.domain.usecase.getinvetories.GetInventoriesInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxDomainInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxInteractor
import com.leinaro.move.domain.usecase.saveinventory.SaveInventoryDomainInteractor
import com.leinaro.move.domain.usecase.saveinventory.SaveInventoryInteractor
import com.leinaro.move.domain.usecase.updateboxstatus.UpdateBoxStatusDomainInteractor
import com.leinaro.move.domain.usecase.updateboxstatus.UpdateBoxStatusInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
  @Binds
  abstract fun bindGetBoxByShortIdInteractor(
    getBoxByShortIdInteractor: GetBoxByShortIdDomainInteractor
  ): GetBoxByShortIdInteractor

  @Binds
  abstract fun bindGetImagesByBoxIdInteractor(
    getImagesByBoxIdInteractor: GetImagesByBoxIdDomainInteractor
  ): GetImagesByBoxIdInteractor

  @Binds
  abstract fun bindSaveImagesInteractor(
    saveImagesInteractor: SaveBoxDomainInteractor
  ): SaveBoxInteractor

  @Binds
  abstract fun bindSaveInventoryInteractor(
    saveInventoryInteractor: SaveInventoryDomainInteractor
  ): SaveInventoryInteractor

  @Binds
  abstract fun bindGetBoxListInteractor(
    getBoxListInteractor: GetBoxListDomainInteractor
  ): GetBoxListInteractor

  @Binds
  abstract fun bindGetBoxListWithInventoryStatusInteractor(
    getBoxListWithInventoryStatusDomainInteractor: GetBoxListWithInventoryStatusDomainInteractor
  ): GetBoxListWithInventoryStatusInteractor

  @Binds
  abstract fun bindUpdateBoxStatusInteractor(
    updateBoxStatusDomainInteractor: UpdateBoxStatusDomainInteractor
  ): UpdateBoxStatusInteractor

  @Binds
  abstract fun bindGetInventoryListDomainInteractor(
    getInventoryListDomainInteractor: GetInventoryListDomainInteractor
  ): GetInventoryListInteractor

  @Binds
  abstract fun bindGetInventoryInteractor(
    getInventoryInteractor: GetInventoryDomainInteractor
  ): GetInventoryInteractor

  @Binds
  abstract fun bindGetInventoryIdUseCase(
    getInventoryIdDomainUseCase: GetInventoryIdDomainUseCase
  ): GetInventoryIdUseCase

  @Binds
  abstract fun bindGetInventoriesInteractor(
    getInventoriesDomainInteractor: GetInventoriesDomainInteractor
  ): GetInventoriesInteractor
}