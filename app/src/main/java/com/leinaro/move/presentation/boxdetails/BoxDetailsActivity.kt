package com.leinaro.move.presentation.boxdetails

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.leinaro.move.BuildConfig
import com.leinaro.move.databinding.ActivityBoxDetailsBinding
import com.leinaro.move.databinding.ActivityMainBinding
import com.leinaro.move.databinding.ImageItemBinding
import com.leinaro.validatable_fields.bindTextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.File

/*inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
  crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
  bindingInflater(layoutInflater)
}*/

@AndroidEntryPoint
class BoxDetailsActivity : AppCompatActivity() {

  private lateinit var binding: ActivityBoxDetailsBinding
//  private val binding by viewBinding(ActivityBoxDetailsBinding::inflate)
  private val viewModel: BoxDetailsViewModel by viewModels()
  private val args: BoxDetailsActivityArgs? by navArgs()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityBoxDetailsBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    //setContentView(binding.root)
    setListener()
    setObserver()
    bindFieldsValidator()

    val action: String? = intent?.action
    val uri: Uri? = intent?.data
    if (uri != null) {
      // DeepLink
      viewModel.onCreate(action, uri)
    } else {
      args?.boxContent?.let { boxContent ->
        viewModel.onCreate(boxContent)
      } ?: run {
      }
    }
  }

  private fun bindFieldsValidator() {
    viewModel.locationValidator.bindTextInputLayout(
      this,
      binding.textFieldLocation,
      isOptional = true,
    )
  }

  private fun takeImage() {
    lifecycleScope.launchWhenStarted {
      getTmpFileUri().let { uri ->
        latestTmpUri = uri
        takeImageResult.launch(uri)
      }
    }
  }

  private val selectImageFromGalleryResult =
    registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList: List<Uri> ->
      viewModel.addImages(uriList.toBitmapList(contentResolver))
    }

  private val takeImageResult =
    registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
      if (isSuccess) {
        latestTmpUri?.let { uri ->
          viewModel.addImages(listOf(uri.toBitmap(contentResolver)))
        }
      }
    }

  private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

  private fun getTmpFileUri(): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
      createNewFile()
      deleteOnExit()
    }

    return FileProvider.getUriForFile(
      applicationContext,
      "${BuildConfig.APPLICATION_ID}.provider",
      tmpFile
    )
  }

  private var latestTmpUri: Uri? = null

  private fun setListener() {
    binding.takePhotoButton.setOnClickListener {
      takeImage()
    }
    binding.galleryButton.setOnClickListener {
      selectImageFromGallery()
    }
    binding.saveButton.setOnClickListener {
      viewModel.save()
    }
  }

  private fun setObserver() {
    this.lifecycleScope.launch {
      viewModel.viewData.filterNotNull()
        .collect {
          binding.textFieldQrCode.text = it.boxContent.uuid
          binding.textFieldLocation.editText?.setText(it.boxContent.location)
          binding.textFieldDescription.editText?.setText(it.boxContent.description)

          with(binding.photos) {
            val bitmapList = it.bitmapList.toMutableList()
            bitmapList.addAll(it.temporalBitmapList)
            this.adapter = CustomAdapter(bitmapList.toTypedArray())
          }
        }
    }
  }
}

fun List<Uri>.toBitmapList(contentResolver: ContentResolver): List<Bitmap> {
  return this.map { uri ->
    uri.toBitmap(contentResolver)
  }
}

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap {
  return if (Build.VERSION.SDK_INT < 28) {
    MediaStore.Images.Media.getBitmap(contentResolver, this)
  } else {
    val source = ImageDecoder.createSource(contentResolver, this)
    ImageDecoder.decodeBitmap(source)
  }
}

class CustomAdapter(private val dataSet: Array<Bitmap>) :
  RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

  class ViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(s: Bitmap) {
      //binding.counter.text = s
      //binding.imageView.setImageURI(Uri.parse(s))
      binding.imageView.setImageBitmap(s)
    }
  }

  // Create new views (invoked by the layout manager)
  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ImageItemBinding.inflate(LayoutInflater.from(viewGroup.context)))
  }

  // Replace the contents of a view (invoked by the layout manager)
  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    viewHolder.bind(dataSet[position])
  }

  // Return the size of your dataset (invoked by the layout manager)
  override fun getItemCount() = dataSet.size

}