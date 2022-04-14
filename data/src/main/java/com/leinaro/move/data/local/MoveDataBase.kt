package com.leinaro.move.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.leinaro.move.data.local.dao.BoxDao
import com.leinaro.move.data.local.dao.ImageDao
import com.leinaro.move.data.local.dao.InventoryDao
import com.leinaro.move.data.local.model.BoxEntity
import com.leinaro.move.data.local.model.ImageEntity
import com.leinaro.move.data.local.model.InventoryEntity
import java.io.ByteArrayOutputStream

@Database(
  entities = [
    BoxEntity::class,
    ImageEntity::class,
    InventoryEntity::class,
  ],
  version = 4,
  exportSchema = true,
  autoMigrations = [
    AutoMigration(from = 2, to = 3),
    AutoMigration(from = 3, to = 4),
  ]
)
@TypeConverters(
  ImageBitmapString::class
) // This will convert Bitmap to String and vice-versa;
//@TypeConverters is defined below
abstract class MoveDataBase : RoomDatabase() {
  abstract fun boxDao(): BoxDao
  abstract fun imageDao(): ImageDao
  abstract fun inventoryDao(): InventoryDao
}

class ImageBitmapString {
  /*@TypeConverter
  fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
  }

  @TypeConverter
  fun getBitmapFromByteArray(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
  }*/

  @TypeConverter
  fun bitMapToString(bitmap: Bitmap): String? {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b = resizeImage(baos.toByteArray())
    val temp = Base64.encodeToString(b, Base64.DEFAULT)
    return temp
  }

  @TypeConverter
  fun stringToBitMap(encodedString: String): Bitmap? {
    try {
      val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
      val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
      return bitmap

    } catch (e: Exception) {
      e.message
      return null
    }
  }

  private fun resizeImage(imageByteArray: ByteArray): ByteArray {
    var imageByteArray = imageByteArray
    while (imageByteArray.size > 500000) {
      val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
      val resized = Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * 0.8).toInt(), (bitmap.height * 0.8).toInt(), true
      )
      val stream = ByteArrayOutputStream()
      resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
      imageByteArray = stream.toByteArray()
    }
    return imageByteArray
  }
}