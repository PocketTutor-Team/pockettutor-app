package com.github.se.project.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationPermissionHandler(onLocationAvailable: (LatLng?) -> Unit) {
  val context = LocalContext.current

  // Launcher that ask user for location permission
  val locationPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted
        -> // isGranted represents the user’s response to the permission request
        if (isGranted) {
          fetchUserLocation(context, onLocationAvailable)
        } else { // Permission denied by the user or the system
          onLocationAvailable(null)
        }
      }

  LaunchedEffect(Unit) {
    // Check if location permission was already granted by the user
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      fetchUserLocation(context, onLocationAvailable)
    } else {
      // In case location permission is not granted, ask user for it
      locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }
}

fun fetchUserLocation(context: android.content.Context, onLocationReceived: (LatLng?) -> Unit) {
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED) {
    fusedLocationClient
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location: Location? ->
          // In case the user has not allow its phone to share its location the location will be
          // latitude and longitude will be null
          if (location != null) {
            onLocationReceived(LatLng(location.latitude, location.longitude))
          } else {
            Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
            onLocationReceived(null)
          }
        }
        .addOnFailureListener {
          Toast.makeText(context, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
          onLocationReceived(null)
        }
  } else {
    Toast.makeText(context, "Location permission was not granted by the user", Toast.LENGTH_SHORT)
        .show()
    onLocationReceived(null)
  }
}
