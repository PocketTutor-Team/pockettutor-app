package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.TopLevelDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LessonsRequestedScreenTest {

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private lateinit var navigationActions: NavigationActions

  private lateinit var mockOnSubjectsSelected: (Set<Subject>) -> Unit
  private lateinit var mockOnDismiss: () -> Unit

  private val mockTutorProfile =
      Profile(
          uid = "100",
          token = "",
          googleUid = "150",
          firstName = "Romeo",
          lastName = "Tutor",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.PHYSICS, Subject.ANALYSIS),
          schedule = List(7) { List(12) { 0 } },
          price = 30)
  private val currentUserFlow = MutableStateFlow(mockTutorProfile)

  private val mockLessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T10:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-12-10T11:00:00",
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "3",
              title = "Instant Tutoring",
              description = "Instantaneous help",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = listOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10Tinstant",
              status = LessonStatus.INSTANT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0))
  private val requestedLessonsFlow = MutableStateFlow(mockLessons)

  // Mock NetworkStatusViewModel to control the network status state
  private val mockIsConnected = MutableStateFlow(true)

  private val mockNetworkStatusViewModel =
      object :
          NetworkStatusViewModel(
              application = androidx.test.core.app.ApplicationProvider.getApplicationContext()) {
        override val isConnected = mockIsConnected
      }

  @Before
  fun setup() {

    // Repositories
    profilesRepository = mock(ProfilesRepository::class.java)
    lessonRepository = mock(LessonRepository::class.java)

    // Initialize the Profile and lesson ViewModel with a spy
    listProfilesViewModel = ListProfilesViewModel(profilesRepository)
    listProfilesViewModel = spy(listProfilesViewModel)

    lessonViewModel = LessonViewModel(lessonRepository)
    lessonViewModel = spy(lessonViewModel)

    navigationActions =
        Mockito.mock(NavigationActions::class.java).apply {
          `when`(currentRoute()).thenReturn(Route.FIND_STUDENT)
        }

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<Lesson>) -> Unit
          onSuccess(mockLessons) // Trigger the success callback with mockLessons
          null
        }
        .`when`(lessonRepository)
        .getAllRequestedLessons(
            org.mockito.kotlin.any<(List<Lesson>) -> Unit>(),
            org.mockito.kotlin.any<(Exception) -> Unit>())

    doReturn(currentUserFlow).`when`(listProfilesViewModel).currentProfile

    `when`(profilesRepository.getProfiles(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
        .thenAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
          onSuccess(listOf())
        }

    mockOnSubjectsSelected = mock()
    mockOnDismiss = mock()
  }

  @Test
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }

    // Verify the lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Instant Tutoring").assertIsNotDisplayed()
  }

  @Test
  fun testNavigation() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }
    composeTestRule.onNodeWithText("Physics Tutoring").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testDatePickerButtonFunctionality() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }

    // Click on the DatePicker to ensure it can be interacted with
    composeTestRule.onNodeWithTag("datePicker").performClick()
  }

  @Test
  fun testBottomNavigationSelection() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }

    // Interact with Bottom Navigation and verify navigation action
    composeTestRule.onNodeWithTag("centerElement").performClick()

    // Verify using the actual TopLevelDestination object
    verify(navigationActions).navigateTo(TopLevelDestinations.TUTOR)
  }

  @Test
  fun testInstantLesson() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }

    // Wait for location to load, then set Instant Filter
    composeTestRule.waitUntil(15000) {
      composeTestRule.onNodeWithTag("instantToggleButton").isDisplayed()
    }
    composeTestRule.onNodeWithTag("instantToggleButton").performClick()

    // Verify the Instant Lesson is displayed
    composeTestRule.onNodeWithText("Instant Tutoring").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsNotDisplayed()

    // Open Distance Filter
    composeTestRule.onNodeWithTag("distanceButton").performClick()

    // Perform the sliding action on the slider
    composeTestRule.onNodeWithTag("distanceSlider").performGesture { swipeRight() }

    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the Instant Lesson is displayed
    composeTestRule.onNodeWithText("Instant Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsNotDisplayed()

    composeTestRule.onNodeWithText("Instant Tutoring").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testFilterDialogInteraction() {
    composeTestRule.setContent {
      RequestedLessonsScreen(
          listProfilesViewModel, lessonViewModel, mockNetworkStatusViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("filterButton").performClick()
    composeTestRule.onNodeWithTag("Physics chip").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Physics chip").performClick()
  }
}
