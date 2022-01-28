package com.leinaro.move.datasource.local

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val DATABASE_NAME = "LeinaroMoveDb"

class MoveDataBaseClient @Inject constructor(
  @ApplicationContext applicationContext: Context
) {
  val db = Room.databaseBuilder(
    applicationContext,
    MoveDataBase::class.java,
    DATABASE_NAME
  )
    .allowMainThreadQueries()
    .build()
}