import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapPickerBox(
    initialLocation: Pair<Double, Double>,
    lessonTitle: String,
    onLocationSelected: (Pair<Double, Double>) -> Unit
) {
    val EPFLCoordinates = LatLng(46.520374, 6.568339)

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

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Map
        Card(
            modifier = Modifier.testTag("map").fillMaxWidth().aspectRatio(8f / 9f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp))
            Text("Confirm Location")
        }
    }
}