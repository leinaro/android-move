package com.leinaro.move.data

import android.content.Context
import androidx.room.Room
import com.leinaro.move.data.local.MoveDataBase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val DATABASE_NAME = "move-database"

class MoveDataBaseClient @Inject constructor(
  @ApplicationContext applicationContext: Context
) {
  val db = Room.databaseBuilder(
    applicationContext,
    MoveDataBase::class.java,
    DATABASE_NAME
  ).build()
}
