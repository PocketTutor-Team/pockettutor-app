package com.github.se.project.model.notification

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun PushNotificationPermissionHandler(onPermissionResult: (Boolean) -> Unit) {
  val context = LocalContext.current

  // Launcher to request notification permission
  val notificationPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          Toast.makeText(context, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
          Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
        onPermissionResult(isGranted)
      }

  LaunchedEffect(Unit) {
    // Check if permission is already granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED) {
      onPermissionResult(true) // Permission already granted
    } else {
      // Request notification permission
      notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }
}
