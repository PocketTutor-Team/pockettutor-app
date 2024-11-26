package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.TopLevelDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class LessonsRequestedScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  private lateinit var lessonRepository: LessonRepository
  private lateinit var lessonViewModel: LessonViewModel

  private lateinit var navigationActions: NavigationActions

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
              timeSlot = "2024-10-20T11:00:00",
              status = LessonStatus.MATCHING,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
                id = "3",
                title = "Programming Instant",
                description = "Object Oriented Programming",
                subject = Subject.ICC,
                languages = listOf(Language.ENGLISH),
                tutorUid = listOf("tutor123"),
                studentUid = "student123",
                minPrice = 20.0,
                maxPrice = 40.0,
                timeSlot = "2024-10-30Tinstant",
                status = LessonStatus.INSTANT_REQUESTED,
                latitude = 3.0,
                longitude = 4.0))
  private val requestedLessonsFlow = MutableStateFlow(mockLessons)

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
        mock(NavigationActions::class.java).apply {
          `when`(currentRoute()).thenReturn(Route.FIND_STUDENT)
        }

    doReturn(requestedLessonsFlow).`when`(lessonViewModel).requestedLessons
    //doNothing().`when`(lessonRepository).getAllRequestedLessons(any(), any())

    //doNothing().`when`(profilesRepository).getProfiles(any(), any())
  }

  @Test
  fun testScreenComponentsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify Top Bar and Bottom Navigation are displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

    @Test
    fun testNoInstantDisplayed() {
        requestedLessonsFlow.value = emptyList() // Set no lessons

        composeTestRule.setContent {
            RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
        Thread.sleep(5000)
        composeTestRule.onNodeWithTag("noInstantsMessage").assertIsDisplayed()
    }

    @Test
    fun testSliderAndInstantLessonDisplay() {
        composeTestRule.setContent {
            RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
        Thread.sleep(5000)
        composeTestRule.onNodeWithTag("distanceButton").performClick()
        //Thread.sleep(5000)
        composeTestRule.onNodeWithTag("distanceSlider").performGesture { swipeRight() }
        composeTestRule.onNodeWithText("Programming Instant").assertIsDisplayed()
    }

  @Test
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

      // Go to non-instant lessons
      Thread.sleep(5000)
      composeTestRule.onNodeWithTag("instantSwitch").performClick()

    // Verify the lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
  }

  @Test
  fun testDatePickerButtonFunctionality() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

      Thread.sleep(5000)
      composeTestRule.onNodeWithTag("instantSwitch").performClick()

    // Click on the DatePicker to ensure it can be interacted with
    composeTestRule.onNodeWithTag("datePicker").performClick()
  }

  @Test
  fun testLessonsFilteredBySelectedDate() {
    // Set the selected date to filter lessons
    val filteredDate = "2024-10-10"

    // Change the lessons to include only those that match the date
    requestedLessonsFlow.value = mockLessons.filter { it.timeSlot.contains(filteredDate) }

    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

      Thread.sleep(5000)
      composeTestRule.onNodeWithTag("instantSwitch").performClick()

    // Verify that only the filtered lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsNotDisplayed()
  }

  @Test
  fun testBottomNavigationSelection() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Interact with Bottom Navigation and verify navigation action
    composeTestRule.onNodeWithTag("Find a Student").performClick()

    // Verify using the actual TopLevelDestination object
    verify(navigationActions).navigateTo(TopLevelDestinations.TUTOR)
  }

  @Test
  fun testNoLessonsMessageDisplayedWhenEmpty() {
    requestedLessonsFlow.value = emptyList() // Set no lessons

    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

      Thread.sleep(5000)
      composeTestRule.onNodeWithTag("instantSwitch").performClick()

    // Verify "No lessons available" message or any placeholder is displayed
    composeTestRule.onNodeWithTag("noLessonsMessage").assertIsDisplayed()
  }
}
