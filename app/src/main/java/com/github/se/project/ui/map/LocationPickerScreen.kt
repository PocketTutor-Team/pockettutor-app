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
  var selectedPosition by remember {
    mutableStateOf(LatLng(initialLocation.first, initialLocation.second))
  }
  val markerState = rememberMarkerState(position = selectedPosition)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(selectedPosition, 10f)
  }

  Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
    GoogleMap(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
          selectedPosition = latLng
          markerState.position = selectedPosition
        }) {
          Marker(
              state = markerState,
              title = "Selected Location",
              snippet = "Lat: ${selectedPosition.latitude}, Lng: ${selectedPosition.longitude}")
        }

    Button(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        onClick = { onLocationSelected(selectedPosition.latitude to selectedPosition.longitude) }) {
          Text("Confirm Location")
        }
  }
}
