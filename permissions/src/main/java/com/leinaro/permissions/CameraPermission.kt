package com.leinaro.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val APP_PACKAGE = "package"

// region public methods
fun getRequestPermissionLauncher(
  fragment: Fragment,
  granted: (() -> Unit)? = null,
  denied: (() -> Unit)? = null,
): ActivityResultLauncher<String> {
  return fragment.registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      granted?.invoke()
    } else {
      denied?.invoke()
    }
  }
}

fun checkCameraPermission(
  fragment: Fragment,
  requestPermissionLauncher: ActivityResultLauncher<String>,
): Boolean {
  if (!haveCameraPermission(fragment.requireContext())) {
    requestCameraPermission(fragment, requestPermissionLauncher)
    return false
  }
  return true
}
// endregion

private fun requestCameraPermission(
  fragment: Fragment,
  requestPermissionLauncher: ActivityResultLauncher<String>,
) {
  when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    -> {
      showAskPermissionOnSettingsDialog(fragment)
    }
    else -> {
      showAskPermissionDialog(fragment, requestPermissionLauncher)
    }
  }
}

private fun haveCameraPermission(context: Context): Boolean {
  return ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.CAMERA
  ) == PackageManager.PERMISSION_GRANTED
}

private fun showAskPermissionOnSettingsDialog(
  fragment: Fragment,
) {
  fragment.context?.let { context ->
    MaterialAlertDialogBuilder(context)
      .setTitle(R.string.permission_title)
      .setMessage(R.string.camera_permission_on_settings_message)
      .setPositiveButton(R.string.settings_action) { dialog, which ->
        fragment.startActivity(
          Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(APP_PACKAGE, fragment.activity?.packageName, null)
          )
        )
      }
      .setNegativeButton(R.string.not_now_action, null)
      .show()
  }
}

private fun showAskPermissionDialog(
  fragment: Fragment,
  requestPermissionLauncher: ActivityResultLauncher<String>? = null,
) {
  fragment.context?.let { context ->
    MaterialAlertDialogBuilder(context)
      .setTitle(R.string.permission_title)
      .setMessage(R.string.camera_permission_message)
      .setPositiveButton(R.string.continue_action) { dialog, which ->
        requestPermissionLauncher?.launch(Manifest.permission.CAMERA)
      }
      .setNegativeButton(R.string.not_now_action, null)
      .show()
  }
}