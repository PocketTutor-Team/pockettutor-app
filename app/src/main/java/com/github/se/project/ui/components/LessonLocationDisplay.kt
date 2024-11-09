package com.github.se.project.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLngBounds

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
    val context = LocalContext.current

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val lessonLocation = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lessonLocation, 10f)
    }

    // Request for location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchUserLocation(context) { location ->
                //location is null currently
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)


                    // Call the utility function to update the camera position
                    cameraPositionState.position = getCameraPositionForBothLocations(
                        userLocation = userLocation,
                        lessonLocation = lessonLocation,
                        currentCameraPosition = cameraPositionState.position
                    )
                }
            }
        }
    }

    // Request location permission if not granted
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // If the permission is already granted (user location is known), update the camera position
    LaunchedEffect(userLocation) {
        // If the user location is available and it's not null
        if (userLocation != null) {
            cameraPositionState.position = getCameraPositionForBothLocations(
                userLocation = userLocation,
                lessonLocation = lessonLocation,
                currentCameraPosition = cameraPositionState.position
            )
        }
    }

    // Map UI
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Lesson Location", style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true), // Automatically show user's location
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        rotationGesturesEnabled = false
                    )
                ) {
                    // Marker for the lesson location
                    Marker(state = MarkerState(position = lessonLocation), title = lessonTitle)


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
    currentCameraPosition: CameraPosition? = null
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
    val bounds = LatLngBounds(
        LatLng(latMin, lonMin),
        LatLng(latMax, lonMax)
    )
    //adjust the zoom level to fit both locations



    // Adjust the camera to fit both locations
    return CameraPosition.fromLatLngZoom(bounds.center, 10f) // Set a zoom level that works for both locations
}

/**
 * Fetches the user's current location using the FusedLocationProviderClient.
 */
private fun fetchUserLocation(context: android.content.Context, onLocationReceived: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        // Permission is granted, get the location
        fusedLocationClient.getCurrentLocation(Priority. PRIORITY_HIGH_ACCURACY , null)
            .addOnSuccessListener { location: Location? ->
                //TODO: In case the user has not allow its phone to share its location the location will be null
                Toast.makeText(context, "User location received", Toast.LENGTH_SHORT).show()
                onLocationReceived(location)
            }
            .addOnFailureListener {
                Toast.makeText(context, "User location not available", Toast.LENGTH_SHORT).show()
                onLocationReceived(null)
            }
    } else {
        // If permission is not granted, return null
        Toast.makeText(context, "Permission is not granted", Toast.LENGTH_SHORT).show()
        onLocationReceived(null)
    }
}

