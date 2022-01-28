package com.leinaro.move.domain.repository.di

import com.leinaro.move.domain.repository.LocalRepository
import com.leinaro.move.domain.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds
  abstract fun bindLocalRepository(
    repository: Repository
  ): LocalRepository
}