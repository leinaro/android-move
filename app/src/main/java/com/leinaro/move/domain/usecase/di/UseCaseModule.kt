package com.leinaro.move.domain.usecase.di

import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdDomainInteractor
import com.leinaro.move.domain.usecase.geimagesbyboxid.GetImagesByBoxIdInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdDomainInteractor
import com.leinaro.move.domain.usecase.getboxbyshortid.GetBoxByShortIdInteractor
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListDomainInteractor
import com.leinaro.move.domain.usecase.getboxlist.GetBoxListInteractor
import com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus.GetBoxListWithInventoryStatusDomainInteractor
import com.leinaro.move.domain.usecase.getboxlistwithinvetorystatus.GetBoxListWithInventoryStatusInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxDomainInteractor
import com.leinaro.move.domain.usecase.saveimages.SaveBoxInteractor
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
}