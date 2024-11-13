import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.ProfilesRepositoryFirestore
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.lesson.ConfirmedLessonScreen
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
public class ConfirmedLessonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val mockLessonRepository = mock(LessonRepository::class.java)

    // Mock dependencies
    val mockProfilesRepository = mock(ProfilesRepository::class.java)
    val mockNavigationActions = mock(NavigationActions::class.java)
    val mockLessonViewModel = LessonViewModel(mockLessonRepository)
    val mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)


    private val mockStudentProfile = Profile(
        uid = "12345",
        googleUid = "67890",
        firstName = "John",
        lastName = "Doe",
        phoneNumber = "1234567890",
        role = Role.TUTOR,
        section = Section.GM,
        academicLevel = AcademicLevel.MA2,
        schedule = List(7) { List(12) { 0 } },
        price = 50
    )

    private val mockLesson = Lesson(
        id = "lesson1",
        title = "Math Lesson",
        timeSlot = "30/12/2024T14:00:00",
        tutorUid = listOf("tutor1"),
        studentUid = "1",
        latitude = 37.7749,
        longitude = -122.4194,
        status = LessonStatus.CONFIRMED
    )

    @Before
    fun setUp() {


        // Mocking the getProfiles function to return a successful result
        whenever(mockProfilesRepository.getProfiles(any(), any())).thenAnswer { invocation ->
            val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
            onSuccess(listOf(mockStudentProfile))
        }
    }

    @Test
    fun confirmedLessonScreen_everythingDisplayedCorrectly() {
        composeTestRule.setContent {
            ConfirmedLessonScreen(
                listProfilesViewModel = mockListProfilesViewModel,
                lessonViewModel = mockLessonViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
        composeTestRule.onNodeWithText("Math Lesson").assertIsDisplayed()
        composeTestRule.onNodeWithTag("contactButton").assertIsDisplayed()
    }

    @Test
    fun confirmedLessonScreen_navigatesBack_whenBackButtonClicked() {
        composeTestRule.setContent {
            ConfirmedLessonScreen(
                listProfilesViewModel = mockListProfilesViewModel,
                lessonViewModel = mockLessonViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithTag("backButton").performClick()
        verify(mockNavigationActions).goBack()
    }

    @Test
    fun confirmedLessonScreen_opensSmsApp_whenContactButtonClicked() {
        composeTestRule.setContent {
            ConfirmedLessonScreen(
                listProfilesViewModel = mockListProfilesViewModel,
                lessonViewModel = mockLessonViewModel,
                navigationActions = mockNavigationActions
            )
        }

        // Click on the "Message Tutor/Student" button
        composeTestRule.onNodeWithTag("contactButton").performClick()
    }

    @Test
    fun confirmedLessonScreen_displaysError_whenNoProfileFound() {
        // Mock no profile found
        whenever(mockListProfilesViewModel.currentProfile).thenReturn(MutableStateFlow(null))

        composeTestRule.setContent {
            ConfirmedLessonScreen(
                listProfilesViewModel = mockListProfilesViewModel,
                lessonViewModel = mockLessonViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithText("No profile found. Should not happen.").assertIsDisplayed()
    }

    @Test
    fun confirmedLessonScreen_displaysError_whenNoLessonSelected() {
        // Mock no lesson selected
        whenever(mockLessonViewModel.selectedLesson).thenReturn(MutableStateFlow(null))

        composeTestRule.setContent {
            ConfirmedLessonScreen(
                listProfilesViewModel = mockListProfilesViewModel,
                lessonViewModel = mockLessonViewModel,
                navigationActions = mockNavigationActions
            )
        }

        composeTestRule.onNodeWithText("No lesson selected. Should not happen.").assertIsDisplayed()
    }
}