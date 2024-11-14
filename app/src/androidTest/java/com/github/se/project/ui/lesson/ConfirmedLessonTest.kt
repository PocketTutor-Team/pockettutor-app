import android.util.Log
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.lesson.ConfirmedLessonScreen
import com.github.se.project.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
public class ConfirmedLessonTest {

  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  val mockLessonRepository = mock(LessonRepository::class.java)

  // Mock dependencies
  val mockProfilesRepository = mock(ProfilesRepository::class.java)
  val mockNavigationActions = mock(NavigationActions::class.java)
  val lessonViewModel = LessonViewModel(mockLessonRepository)
  val listProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)

  private val tutorProfile =
      Profile(
          uid = "12345",
          googleUid = "67890",
          firstName = "John",
          lastName = "Doe",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)

  private val studentProfile =
      Profile(
          uid = "1",
          googleUid = "67890",
          firstName = "James",
          lastName = "Donovan",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.GM,
          academicLevel = AcademicLevel.BA2,
          schedule = List(7) { List(12) { 0 } },
          price = 50)

  private val lesson =
      Lesson(
          id = "lesson1",
          title = "Math Lesson",
          timeSlot = "30/12/2024T14:00:00",
          tutorUid = listOf("tutor1"),
          studentUid = "1",
          latitude = 37.7749,
          longitude = -122.4194,
          status = LessonStatus.CONFIRMED)

  @Before
  fun setUp() {
    whenever(mockProfilesRepository.getProfiles(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
          onSuccess(
              listOf(
                  tutorProfile,
                  studentProfile)) // Simulate a list of profiles with our beloved Ozymandias
        }

    whenever(
            mockLessonRepository.getLessonsForTutor(
                org.mockito.kotlin.eq(tutorProfile.uid),
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
          onSuccess(listOf(lesson))
        }

    listProfilesViewModel.getProfiles()
    lessonViewModel.getLessonsForTutor(tutorProfile.uid, {})

    lessonViewModel.selectLesson(lesson)
    listProfilesViewModel.setCurrentProfile(tutorProfile)
    Log.e("LeProfileBG", "${listProfilesViewModel.currentProfile}")
  }

  @Test
  fun confirmedLessonScreenEverythingDisplayedCorrectly() {
    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }
    Thread.sleep(5000)

    composeTestRule.onNodeWithTag("confirmedLessonScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("backButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Lesson").assertIsDisplayed()
    composeTestRule.onNodeWithTag("contactButton").assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenBackButtonClicked() {
    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("backButton").performClick()
    verify(mockNavigationActions).goBack()
  }

  @Test
  fun confirmedLessonScreenOpensSmsApp() {
    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }

    // Click on the "Message Tutor/Student" button
    composeTestRule.onNodeWithTag("contactButton").performClick()
  }

  @Test
  fun confirmedLessonScreenNoProfileFound() {
    // Mock no profile found
    listProfilesViewModel.setCurrentProfile(null)

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("No profile found. Should not happen.").assertIsDisplayed()
  }

  @Test
  fun confirmedLessonScreenNoLessonSelected() {
    // Mock no lesson selected
    lessonViewModel.unselectLesson()

    composeTestRule.setContent {
      ConfirmedLessonScreen(
          listProfilesViewModel = listProfilesViewModel,
          lessonViewModel = lessonViewModel,
          navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("No lesson selected. Should not happen.").assertIsDisplayed()
  }
}
