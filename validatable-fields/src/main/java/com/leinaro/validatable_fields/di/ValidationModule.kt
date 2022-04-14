package com.leinaro.validatable_fields.di

import com.leinaro.validatable_fields.validators.AppNotEmptyValidator
import com.leinaro.validatable_fields.validators.NotEmptyValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {

  @Provides
  fun providesNotEmptyValidator(): NotEmptyValidator = AppNotEmptyValidator

}