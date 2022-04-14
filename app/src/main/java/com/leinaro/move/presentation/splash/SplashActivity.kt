package com.leinaro.move.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.leinaro.move.BuildConfig
import com.leinaro.move.R
import com.leinaro.move.databinding.ActivitySplashBinding
import com.leinaro.move.presentation.main.MainActivity

private const val UI_ANIMATION_DELAY = 1000L

class SplashActivity : AppCompatActivity() {

  private lateinit var binding: ActivitySplashBinding

  private val handler = Handler(Looper.getMainLooper())
  private val showAppInfoRunnable = Runnable { showAppInfo() }
  private val startMainActivityRunnable = Runnable { startMainActivity() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySplashBinding.inflate(layoutInflater)
    setContentView(binding.root)
  //  throw RuntimeException("Test Crash") // Force a crash
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    delayedShowAppInfo()
  }

  private fun delayedShowAppInfo(delayMillis: Long = UI_ANIMATION_DELAY) {
    handler.removeCallbacks(showAppInfoRunnable)
    handler.postDelayed(showAppInfoRunnable, delayMillis)
  }

  private fun showAppInfo() {
    binding.version.text = BuildConfig.VERSION_NAME
    binding.fullscreenContentControls.isVisible = true

    // Schedule a runnable to remove the status and navigation bar after a delay
    handler.removeCallbacks(showAppInfoRunnable)
    handler.postDelayed(startMainActivityRunnable, UI_ANIMATION_DELAY)
  }

  private fun startMainActivity() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
  }
}