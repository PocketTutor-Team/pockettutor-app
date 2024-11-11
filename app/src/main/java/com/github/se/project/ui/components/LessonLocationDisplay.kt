package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.se.project.ui.map.LocationPermissionHandler
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * A component that displays a Google Map showing the lesson location and user's location
 *
 * @param latitude The latitude coordinate of the lesson location
 * @param longitude The longitude coordinate of the lesson location
 * @param lessonTitle The title for the lesson
 * @param modifier Optional modifier for the component
 */
@Composable
fun LessonLocationDisplay(
    latitude: Double,
    longitude: Double,
    lessonTitle: String,
    modifier: Modifier = Modifier
) {
  var userLocation by remember { mutableStateOf<LatLng?>(null) }
  var isLocationChecked by remember { mutableStateOf(false) }

  val lessonLocation = LatLng(latitude, longitude)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(lessonLocation, 10f)
  }

  // Request for location permission launcher
  LocationPermissionHandler { location ->
    userLocation = location
    isLocationChecked = true

    // Update the camera position based on user location availability
    cameraPositionState.position =
        if (location != null) {
          getCameraPositionForBothLocations(
              userLocation = location, lessonLocation = lessonLocation)
        } else {
          CameraPosition.fromLatLngZoom(lessonLocation, 10f)
        }
  }

  // Map UI
  if (isLocationChecked) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(text = "Lesson Location", style = MaterialTheme.typography.titleMedium)

      Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
              GoogleMap(
                  modifier = Modifier.fillMaxSize(),
                  cameraPositionState = cameraPositionState,
                  properties = MapProperties(isMyLocationEnabled = (userLocation != null)),
                  uiSettings =
                      MapUiSettings(
                          zoomControlsEnabled = false,
                          scrollGesturesEnabled = false,
                          zoomGesturesEnabled = false,
                          tiltGesturesEnabled = false,
                          rotationGesturesEnabled = false)) {
                    // Marker for the lesson location
                    Marker(state = MarkerState(position = lessonLocation), title = lessonTitle)
                  }
            }
          }
    }
  }
}

/**
 * Utility function to calculate the bounds to show both the user's location and lesson location,
 * and update the camera position to fit them.
 */
fun getCameraPositionForBothLocations(
    userLocation: LatLng?,
    lessonLocation: LatLng,
): CameraPosition {
  // If we don't have the user's location, just focus on the lesson location
  if (userLocation == null) {
    return CameraPosition.fromLatLngZoom(lessonLocation, 10f)
  }

  // Otherwise, calculate the bounds to fit both locations
  val latitudes = listOf(userLocation.latitude, lessonLocation.latitude)
  val longitudes = listOf(userLocation.longitude, lessonLocation.longitude)

  // Get the bounds to show both locations
  val latMin = latitudes.minOrNull() ?: userLocation.latitude
  val latMax = latitudes.maxOrNull() ?: userLocation.latitude
  val lonMin = longitudes.minOrNull() ?: userLocation.longitude
  val lonMax = longitudes.maxOrNull() ?: userLocation.longitude

  // Create the bounds
  val bounds = LatLngBounds(LatLng(latMin, lonMin), LatLng(latMax, lonMax))
  // adjust the zoom level to fit both locations

  // Adjust the camera to fit both locations
  return CameraPosition.fromLatLngZoom(
      bounds.center, 10f) // Set a zoom level that works for both locations
}
