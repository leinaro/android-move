package com.leinaro.move.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
//  private val saveElementToPayInteractor: SaveElementToPayInteractor,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  fun onCreate() {
    viewModelScope.launch(Dispatchers.IO) {
    }
  }
}
