package com.leinaro.move.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.leinaro.architecture_tools.setObserver
import com.leinaro.move.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  lateinit var binding: ActivityMainBinding

//  private val binding by viewBinding(ActivityMainBinding::inflate)

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
    setObserver(viewModel)
    viewModel.onCreate()
  }
}