import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.ui.map.LocationPermissionHandler
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


/**
 * A composable that displays a map picker box. The user can select a location on the map by
 * tapping on it. The selected location is then returned to the caller.
 *
 * @param initialLocation The initial location to display on the map.
 * @param onLocationSelected The callback to be called when a location is selected.
 * @param onMapReady The callback to be called when the map is loaded and displayed.
 */
@Composable
fun LocationPickerBox(
    initialLocation: Pair<Double, Double>,
    onLocationSelected: (Pair<Double, Double>) -> Unit,
    onMapReady: (Boolean) -> Unit = {}
) {
  val EPFLCoordinates = LatLng(46.520374, 6.568339)

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

  var selectedPosition by remember {
    mutableStateOf(LatLng(initialLocation.first, initialLocation.second))
  }
  val markerState = rememberMarkerState(position = selectedPosition)


  val cameraPositionState = rememberCameraPositionState {
    position =
        if (selectedPosition.latitude == 0.0 && selectedPosition.longitude == 0.0) {
          CameraPosition.fromLatLngZoom(EPFLCoordinates, 15f)
        } else {
          CameraPosition.fromLatLngZoom(selectedPosition, 15f)
        }
  }


    //Fetch the user's location and update the map's camera and marker state with it
    LocationPermissionHandler { fetchedLocation ->
        fetchedLocation?.let {
            // Update map's camera and marker state with the user's location
            userLocation = it
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
        } ?: run {
            Log.e("LocationPickerBox", "User location not available.")
        }
    }

  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Map
        Card(
            modifier = Modifier.testTag("mapContainer").fillMaxWidth().aspectRatio(8f / 9f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
              GoogleMap(
                  modifier = Modifier.fillMaxSize().testTag("googleMap"),
                  cameraPositionState = cameraPositionState,
                  properties = MapProperties(isMyLocationEnabled = userLocation != null),
                  onMapLoaded = {
                    onMapReady(true)
                    Log.e("MapPickerBox", "Map loaded")
                  },
                  onMapClick = { latLng ->
                    selectedPosition = latLng
                    markerState.position = selectedPosition
                  }) {
                    if (selectedPosition.latitude != 0.0 || selectedPosition.longitude != 0.0) {
                      Marker(state = markerState, title = "Lesson Location")
                    }
                  }
            }

        // Helper text
        Text(
            text =
                if (selectedPosition.latitude != 0.0 || selectedPosition.longitude != 0.0)
                    "Location selected! Tap confirm to use this location."
                else "Tap anywhere on the map to select a location",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.testTag("helperText"))

        // Confirm button
        Button(
            onClick = {
              onLocationSelected(selectedPosition.latitude to selectedPosition.longitude)
            },
            modifier = Modifier.testTag("confirmLocation").fillMaxWidth(),
            enabled = selectedPosition.latitude != 0.0 || selectedPosition.longitude != 0.0,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant)) {
              Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = "Confirm Location",
                  modifier = Modifier.padding(end = 8.dp))
              Text(stringResource(id = R.string.confirm_location))
            }
      }
}
