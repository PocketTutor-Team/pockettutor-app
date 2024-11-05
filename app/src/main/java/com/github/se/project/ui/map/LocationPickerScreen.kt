import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapPickerBox(
    initialLocation: Pair<Double, Double>,
    onLocationSelected: (Pair<Double, Double>) -> Unit
) {
  val EPFLCoordinates = LatLng(46.520374, 6.568339)

  var selectedPosition by remember {
    mutableStateOf(LatLng(initialLocation.first, initialLocation.second))
  }
  val markerState = rememberMarkerState(position = selectedPosition)

  val cameraPositionState = rememberCameraPositionState {
    position =
        // If no location is selected yet, set the camera position on the EPFL
        if (selectedPosition.latitude == 0.0 && selectedPosition.longitude == 0.0) {
          CameraPosition.fromLatLngZoom(EPFLCoordinates, 10f)
        } else {
          CameraPosition.fromLatLngZoom(selectedPosition, 10f)
        }
  }

  Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
    GoogleMap(
        modifier = Modifier.fillMaxWidth().height(400.dp),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
          selectedPosition = latLng
          markerState.position = selectedPosition
        }) {
          // Display marker only if initialLocation is not (0, 0)
          if (selectedPosition.latitude != 0.0 || selectedPosition.longitude != 0.0) {
            Marker(
                state = markerState,
                title = "Location of the Lesson",
            )
          }
        }

    Button(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        onClick = { onLocationSelected(selectedPosition.latitude to selectedPosition.longitude) }) {
          Text("Confirm Location")
        }
  }
}
