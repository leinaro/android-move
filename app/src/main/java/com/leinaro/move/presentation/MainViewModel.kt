package com.leinaro.move.presentation

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leinaro.move.datasource.DataBaseClient
import com.leinaro.move.datasource.local.dao.BoxDao
import com.leinaro.move.datasource.local.model.BoxEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
//  private val saveElementToPayInteractor: SaveElementToPayInteractor,
  private val dataBase: DataBaseClient,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  /*val elements = mutableMapOf<String, BoxContent>(
    "ab07840d" to BoxContent(
      uuid = "ab07840d-48f7-446f-bae2-9eb85bd8c860",
      uuidShort = "ab07840d",
      counter = 1,
      location = "Sala"
    ),
    "74db2ad2" to BoxContent(
      uuid = "74db2ad2-8994-4dde-9ec3-7013769eef42",
      uuidShort = "74db2ad2",
      counter = 2,
      location = "Sala"
    ),
    "993c2409" to BoxContent(
      uuid = "993c2409-f2b8-4bd6-9928-4a6668daa77c",
      uuidShort = "993c2409",
      counter = 3,
      location = "Leo",
      description = "Equipo, musica, warcraft",
    ),
    "a1e62e7c" to BoxContent(
      uuid = "a1e62e7c-8a75-4138-af20-28a28573622c",
      uuidShort = "a1e62e7c",
      counter = 4,
      location = "Leo"
    ),
    "40458f06" to BoxContent(
      uuid = "40458f06-9ca5-4166-b23f-e85981499347",
      uuidShort = "40458f06",
      counter = 5,
      location = "Sala"
    ),
    "40f5a6fc" to BoxContent(
      uuid = "40f5a6fc-b881-443d-b94f-2d5af5fca589",
      uuidShort = "40f5a6fc",
      counter = 6,
      location = "Sala"
    ),
    "c4b4e8b7" to BoxContent(
      uuid = "c4b4e8b7-00f0-4cac-b4cd-f876190c97a7",
      uuidShort = "c4b4e8b7",
      counter = 7,
      location = "Leo",
      description = "nerf, aviones y carros",
    ),
    "8450ec81" to BoxContent(
      uuid = "8450ec81-627b-4c63-ae06-27d7450b9235",
      uuidShort = "8450ec81",
      counter = 8,
      location = "Leo"
    ),
    "2039df98" to BoxContent(
      uuid = "2039df98-63d1-498e-9d3e-3cdb3c42f74a",
      uuidShort = "2039df98",
      counter = 9,
      location = "Biblioteca",
      description = "libros"
    ),
    "c5bb7872" to BoxContent(
      uuid = "c5bb7872-6b62-4c11-9c68-a5c2eb21a245",
      uuidShort = "c5bb7872",
      counter = 10,
      location = "Biblioteca",
      description = "libros"
    ),
  )*/

  val boxEntityList = listOf(
    BoxEntity(
      uuid = "ab07840d",
      counter = 1,
      location = "Sala",
      description = ""
    ).apply { id = 0 },
    BoxEntity(
      uuid = "74db2ad2",
      counter = 2,
      location = "Sala",
      description = ""
    ).apply { id = 1 },
    BoxEntity(
      uuid = "993c2409",
      counter = 3,
      location = "Leo",
      description = "Equipo, musica, warcraft",
    ).apply { id = 2 },
    BoxEntity(
      uuid = "a1e62e7c",
      counter = 4,
      location = "Leo",
      description = ""
    ).apply { id = 3 },
    BoxEntity(
      uuid = "40458f06",
      counter = 5,
      location = "Sala",
      description = ""
    ).apply { id = 4 },
    BoxEntity(
      uuid = "40f5a6fc",
      counter = 6,
      location = "Sala",
      description = ""
    ).apply { id = 5 },
    BoxEntity(
      uuid = "c4b4e8b7",
      counter = 7,
      location = "Leo",
      description = "nerf, aviones y carros",
    ).apply { id = 6 },
    BoxEntity(
      uuid = "8450ec81",
      counter = 8,
      location = "Leo",
      description = ""
    ).apply { id = 7 },
    BoxEntity(
      uuid = "2039df98",
      counter = 9,
      location = "Biblioteca",
      description = "libros",
    ).apply { id = 8 },
    BoxEntity(
      uuid = "c5bb7872",
      counter = 10,
      location = "Biblioteca",
      description = "libros",
    ).apply { id = 9 },
  )

  fun onCreate(context: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      /*val boxEntityList = elements.map {
        it.value.toBoxEntity()
      }*/
      val boxDao: BoxDao = dataBase.db.boxDao()
      boxDao.insert(*boxEntityList.toTypedArray())
    }
  }
}
