import com.github.se.project.ui.components.adjustZoomBasedOnDistance
import com.github.se.project.ui.components.calculateDistance
import com.github.se.project.ui.components.getCameraPositionForBothLocations
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test


class CameraUtilsTest {

    // Helper locations for testing
    private val lessonLocation = LatLng(40.748817, -73.985428) // Example: NYC
    private val nearbyUserLocation = LatLng(40.748817, -73.985500) // Nearby location
    private val distantUserLocation = LatLng(34.052235, -118.243683) // Distant location, LA

    @Test
    fun `when userLocation is null, should return CameraPosition focused on lessonLocation with default zoom`() {
        val cameraPosition = getCameraPositionForBothLocations(null, lessonLocation)
        assertEquals(lessonLocation, cameraPosition.target)
        assertEquals(10f, cameraPosition.zoom, 0.1f)
    }

    @Test
    fun `when userLocation is nearby lessonLocation, should return high zoom level`() {
        val cameraPosition = getCameraPositionForBothLocations(nearbyUserLocation, lessonLocation)
        val expectedZoomLevel = adjustZoomBasedOnDistance(
            calculateDistance(nearbyUserLocation, lessonLocation)
        )
        assertEquals(expectedZoomLevel, cameraPosition.zoom, 0.1f)
    }

    @Test
    fun `when userLocation is distant from lessonLocation, should return lower zoom level`() {
        val cameraPosition = getCameraPositionForBothLocations(distantUserLocation, lessonLocation)
        val expectedZoomLevel = adjustZoomBasedOnDistance(
            calculateDistance(distantUserLocation, lessonLocation)
        )
        assertEquals(expectedZoomLevel, cameraPosition.zoom, 0.1f)
    }

    @Test
    fun `calculateDistance returns correct distance in meters`() {
        val distance = calculateDistance(
            LatLng(40.748817, -73.985428), // NYC
            LatLng(34.052235, -118.243683) // LA
        )
        assertEquals(3944000f, distance, 50000f) // Expected ~3944km with margin for accuracy
    }

    @Test
    fun `adjustZoomBasedOnDistance returns correct zoom level`() {
        assertEquals(15f, adjustZoomBasedOnDistance(50f))
        assertEquals(14f, adjustZoomBasedOnDistance(200f))
        assertEquals(12f, adjustZoomBasedOnDistance(1500f))
        assertEquals(10f, adjustZoomBasedOnDistance(5000f))
        assertEquals(8f, adjustZoomBasedOnDistance(15000f))
        assertEquals(7f, adjustZoomBasedOnDistance(30000f))
        assertEquals(6f, adjustZoomBasedOnDistance(80000f))
        assertEquals(5f, adjustZoomBasedOnDistance(120000f))
    }
}
