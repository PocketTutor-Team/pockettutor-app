import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapPickerScreen(
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
    var selectedPosition by remember {
        mutableStateOf(
            LatLng(
                lessonViewModel.selectedLocation.value.first,
                lessonViewModel.selectedLocation.value.second
            )
        )
    }

    val markerState = rememberMarkerState(position = selectedPosition)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedPosition, 10f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Take available space except the button area
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedPosition = latLng
                markerState.position = selectedPosition // Update the marker position
            }
        ) {

            Marker(
                state = markerState,
                title = "Selected Location",
                snippet = "Lat: ${selectedPosition.latitude}, Lng: ${selectedPosition.longitude}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // Customize the icon
            )
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            onClick = {

                lessonViewModel.updateSelectedLocation(selectedPosition.latitude to selectedPosition.longitude)

                navigationActions.navigateTo(Screen.ADD_LESSON)
            }
        ) {
            Text("Confirm Location")
        }
    }
}
