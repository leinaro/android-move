package com.leinaro.move

import java.io.Serializable

data class BoxContent(
  val uuid: String,
  val counter: Int,
  val location: String,
  val description: String = "",
  val photoPath: String = "",
  val inventoried: Boolean,
) : Serializable