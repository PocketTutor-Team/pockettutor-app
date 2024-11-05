package com.github.se.project.ui.overview

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class HomeScreenTest {

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
              status = LessonStatus.TUTOR_REQUESTED,
              latitude = 0.0,
              longitude = 0.0),
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
              status = LessonStatus.STUDENT_REQUESTED,
              latitude = 0.0,
              longitude = 0.0))
  private val currentUserLessonsFlow = MutableStateFlow<List<Lesson>>(mockLessons)

  @Before
  fun setup() {
    // Mocking the ViewModels
    listProfilesViewModel =
        Mockito.mock(ListProfilesViewModel::class.java).apply {
          Mockito.`when`(currentProfile).thenReturn(MutableStateFlow(profile))
        }
    val mockRepository = mock<LessonRepository>()
    val currentUserLessonsFlow = MutableStateFlow<List<Lesson>>(mockLessons) // or your initial data
    lessonViewModel =
        Mockito.spy(LessonViewModel(mockRepository)).apply {
          // Assuming currentUserLessons is initialized in the constructor or some method,
          // you'll have to use reflection to set it if it's private or internal.
          `when`(this.currentUserLessons).thenReturn(currentUserLessonsFlow)
        }
    navigationActions =
        Mockito.mock(NavigationActions::class.java).apply {
          // Ensure that currentRoute() returns a valid string
          Mockito.`when`(currentRoute()).thenReturn(Route.HOME) // or whatever the default route is
        }
  }

  @Test
  fun testIsDiplayed() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Profile Icon").assertIsDisplayed()
  }

  @Test
  fun testProfileIconClickable() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }
    composeTestRule.onNodeWithContentDescription("Profile Icon").performClick()
    verify(navigationActions).navigateTo(Mockito.anyString())
  }

  @Test
  fun testLessonItemsDisplayed() {
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }
    composeTestRule.onNodeWithText("Physics Tutoring").assertIsDisplayed()
  }

  @Test
  fun testNoProfileFoundScreenDisplayed() {
    // Set a null profile in the ViewModel
    listProfilesViewModel =
        Mockito.mock(ListProfilesViewModel::class.java).apply {
          Mockito.`when`(currentProfile).thenReturn(MutableStateFlow(null))
        }
    // Recompose
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify that the "No profile is currently assigned" text is displayed
    composeTestRule
        .onNodeWithText("No profile is currently assigned to the current user.")
        .assertIsDisplayed()

    // Verify the button to go back to HOME screen is displayed
    composeTestRule.onNodeWithText("Go back to HOME screen").assertIsDisplayed()
  }

  @Test
  fun testLessonsEmptyMessageDisplayed() {
    // Set up empty lessons scenario
    val mockRepository = mock<LessonRepository>()
    currentUserLessonsFlow.value = emptyList() // Simulate no lessons
    lessonViewModel =
        Mockito.spy(LessonViewModel(mockRepository)).apply {
          // Assuming currentUserLessons is initialized in the constructor or some method,
          // you'll have to use reflection to set it if it's private or internal.
          `when`(this.currentUserLessons).thenReturn(currentUserLessonsFlow)
        }
    composeTestRule.setContent {
      HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
    }

    // Verify the message indicating no lessons scheduled is displayed
    composeTestRule.onNodeWithTag("noLessonsText").assertIsDisplayed()
  }
}
