package com.leinaro.move.presentation.data

import java.io.Serializable

data class BoxContent(
  val uuid: String,
  val counter: Int = -1,
  val location: String = "",
  val description: String = "",
  val photoPath: String = "",
  val inventoried: Boolean = false,
  val isNew: Boolean = false
) : Serializable