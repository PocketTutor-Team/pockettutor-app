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
import androidx.compose.ui.platform.testTag
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A component that displays a map showing the lesson's location and the user's location
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

  if (isLocationChecked) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(text = "Lesson Location", style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.testTag("LessonLocationText"))

      Card(
          modifier = Modifier.fillMaxWidth().testTag("LessonLocationCard"),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).testTag("MapBox") ) {
              GoogleMap(
                  modifier = Modifier.fillMaxSize().testTag("GoogleMap"),
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

  // Calculate the zoom level based on the distance between the two locations
  val distance = calculateDistance(userLocation, lessonLocation)
  val zoomLevel = adjustZoomBasedOnDistance(distance)

  // Adjust the camera to fit both locations with appropriate zoom
  return CameraPosition.fromLatLngZoom(bounds.center, zoomLevel)
}

/** Private function to calculate the distance (in meters) between two locations. */
fun calculateDistance(userLocation: LatLng?, lessonLocation: LatLng): Float {
  if (userLocation == null) return 0f

  val lat1 = userLocation.latitude
  val lon1 = userLocation.longitude
  val lat2 = lessonLocation.latitude
  val lon2 = lessonLocation.longitude

  val earthRadius = 6371 // Radius of the Earth in kilometers
  val latDiff = Math.toRadians(lat2 - lat1)
  val lonDiff = Math.toRadians(lon2 - lon1)

  val a =
      sin(latDiff / 2).pow(2.0) +
          cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(lonDiff / 2).pow(2.0)
  val c = 2 * atan2(sqrt(a), sqrt(1 - a))

  // Distance in kilometers
  val distanceInKm = earthRadius * c

  // Convert to meters
  return (distanceInKm * 1000).toFloat()
}

/**
 * Private function to adjust the zoom level based on the distance between two locations. The
 * further the locations, the lower the zoom level.
 */
fun adjustZoomBasedOnDistance(distance: Float): Float {
  return when {
    distance < 100 -> 15f // If the distance is less than 100 meters, zoom in more
    distance < 500 -> 14f // If the distance is between 100-500 meters, zoom in a bit
    distance < 2000 -> 12f // If the distance is between 500m-2km, zoom out a bit
    distance < 10000 -> 10f // If the distance is between 2km-10km, zoom out even more
    distance < 20000 -> 8f // If the distance is between 10km-20km, zoom out further
    distance < 50000 -> 7f // If the distance is between 20km-50km, zoom out even further
    distance < 100000 -> 6f // If the distance is between 50km-100km, zoom out even further
    else -> 5f // If the distance is more than 100km, zoom out further
  }
}
