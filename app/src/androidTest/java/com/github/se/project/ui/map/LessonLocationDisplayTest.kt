import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.ui.components.LessonLocationDisplay
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class LessonLocationDisplayTest {

    @Mock
    lateinit var mockContext: Context

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock the context to simulate different permission handling
        mockContext = mock(Context::class.java)
    }

    @Test
    fun testLessonLocationDisplay_withLocationPermissionGranted() {
        // Mocking permission granted and returning a simulated user location
        whenever(
            mockContext.checkPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.os.Process.myPid(),
                android.os.Process.myUid()
            )
        ).thenReturn(PackageManager.PERMISSION_GRANTED)

        // Define test data
        val testLatitude = 46.520374
        val testLongitude = 6.568339
        val lessonTitle = "Test Lesson Location"
        val mockUserLocation = LatLng(46.5197, 6.5668) // Simulated user location near the lesson

        // Set the composable in the test environment with mock location handler
        composeTestRule.setContent {
            LessonLocationDisplay(
                latitude = testLatitude,
                longitude = testLongitude,
                lessonTitle = lessonTitle,
                modifier = androidx.compose.ui.Modifier
            )
        }

        // Assert that UI elements display correctly with granted location permissions
        composeTestRule.onNodeWithTag("LessonLocationText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("LessonLocationCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("MapBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GoogleMap").assertIsDisplayed()
    }

    @Test
    fun testLessonLocationDisplay_withLocationPermissionDenied() {
        // Mocking permission denied
        whenever(
            mockContext.checkPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.os.Process.myPid(),
                android.os.Process.myUid()
            )
        ).thenReturn(PackageManager.PERMISSION_DENIED)

        // Define test data
        val testLatitude = 46.520374
        val testLongitude = 6.568339
        val lessonTitle = "Test Lesson Location"

        // Set the composable in the test environment with mock location handler
        composeTestRule.setContent {
            LessonLocationDisplay(
                latitude = testLatitude,
                longitude = testLongitude,
                lessonTitle = lessonTitle,
                modifier = androidx.compose.ui.Modifier
            )
        }

        // Assert that UI elements still display correctly without a user location
        composeTestRule.onNodeWithTag("LessonLocationText").assertIsDisplayed()
        composeTestRule.onNodeWithTag("LessonLocationCard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("MapBox").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GoogleMap").assertIsDisplayed()
    }
}
