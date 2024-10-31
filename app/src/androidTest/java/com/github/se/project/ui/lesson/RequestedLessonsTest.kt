package com.github.se.project.ui.lesson

import RequestedLessonsScreen
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LessonsRequestedScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var listProfilesViewModel: ListProfilesViewModel
  private lateinit var lessonViewModel: LessonViewModel
  private lateinit var navigationActions: NavigationActions

  private val profile =
      Profile(
          "uid",
          "googleUid",
          "firstName",
          "lastName",
          "phoneNumber",
          Role.TUTOR,
          Section.AR,
          AcademicLevel.BA1,
          listOf(Language.ENGLISH),
          listOf(Subject.ANALYSIS),
          List(7) { List(12) { 0 } },
          0)

  private val mockLessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = "tutor123",
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T10:00:00",
              status = LessonStatus.REQUESTED),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = "tutor123",
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "2024-10-10T11:00:00",
              status = LessonStatus.REQUESTED))
  private val requestedLessonsFlow = MutableStateFlow(mockLessons)

  @Before
  fun setup() {
    // Mock the ViewModels
    listProfilesViewModel =
        Mockito.mock(ListProfilesViewModel::class.java).apply {
          `when`(currentProfile).thenReturn(MutableStateFlow(profile))
        }

    val mockRepository = mock(LessonRepository::class.java)
    lessonViewModel =
        Mockito.spy(LessonViewModel(mockRepository)).apply {
          `when`(this.requestedLessons).thenReturn(requestedLessonsFlow)
        }

    navigationActions =
        Mockito.mock(NavigationActions::class.java).apply {
          `when`(currentRoute()).thenReturn(Route.FIND_STUDENT)
        }
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
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
  }

  @Test
  fun testDatePickerButtonFunctionality() {
    composeTestRule.setContent {
      RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

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

    // Verify that only the filtered lesson items are displayed
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Tutoring").assertIsDisplayed()
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

    // Verify "No lessons available" message or any placeholder is displayed
    composeTestRule.onNodeWithTag("noLessonsMessage").assertIsDisplayed()
  }
}
