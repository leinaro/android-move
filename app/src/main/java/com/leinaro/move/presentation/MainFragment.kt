package com.leinaro.move.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.leinaro.move.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

  private var _binding: FragmentMainBinding? = null
  val binding get() = _binding!!

//  private val viewModel: MainFragmentViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setListeners()
  }

  private fun setListeners() {
    binding.button.setOnClickListener { navigateToCaptureActivity() }
    binding.button2.setOnClickListener { navigateToBoxListFragment() }
    binding.button3.setOnClickListener { navigateToInventoryFragment() }
  }

  private fun navigateToCaptureActivity() {
    val directions = MainFragmentDirections.navigateToCaptureActivity()
    NavHostFragment.findNavController(this).navigate(directions)
  }

  private fun navigateToBoxListFragment() {
    val directions = MainFragmentDirections.navigateToBoxListFragment()
    NavHostFragment.findNavController(this).navigate(directions)
  }

  private fun navigateToInventoryFragment() {
    val directions = MainFragmentDirections.navigateToInventoryFragment()
    NavHostFragment.findNavController(this).navigate(directions)
  }

  /* fun encodeAsBitmap(str: String?): Bitmap? {
     var result: BitMatrix
     var bitmap: Bitmap? = null
     try {
       result = MultiFormatWriter().encode(
         str,
         BarcodeFormat.QR_CODE, WIDTH, WIDTH, null
       )
       val w = result.width
       val h = result.height
       val pixels = IntArray(w * h)
       for (y in 0 until h) {
         val offset = y * w
         for (x in 0 until w) {
           pixels[offset + x] = if (result[x, y]) black else white
         }
       }
       bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
       bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h)
     } catch (iae: Exception) {
       iae.printStackTrace()
       return null
     }
     return bitmap
   }*/

}