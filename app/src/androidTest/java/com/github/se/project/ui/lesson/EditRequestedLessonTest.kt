package com.github.se.project.ui.lesson

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.validateLessonInput
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EditRequestedLessonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val navigationActions = mock(NavigationActions::class.java)
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
  private val lessons =
      listOf(
          Lesson(
              id = "1",
              title = "Physics Tutoring",
              description = "Mechanics and Thermodynamics",
              subject = Subject.PHYSICS,
              languages = listOf(Language.ENGLISH),
              tutorUid = mutableListOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "10/10/2024T10:00:00",
              status = LessonStatus.CONFIRMED,
              latitude = 0.0,
              longitude = 0.0),
          Lesson(
              id = "2",
              title = "Math Tutoring",
              description = "Algebra and Calculus",
              subject = Subject.ANALYSIS,
              languages = listOf(Language.ENGLISH),
              tutorUid = mutableListOf("tutor123"),
              studentUid = "student123",
              minPrice = 20.0,
              maxPrice = 40.0,
              timeSlot = "10/10/2024T11:00:00",
              status = LessonStatus.CONFIRMED,
              latitude = 0.0,
              longitude = 0.0))
  private val mockProfiles =
      mock(ListProfilesViewModel::class.java).apply {
        `when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(profile))
      }

  private val mockLessonRepository = mock(LessonRepository::class.java)
  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  @Before
  fun setup() {
    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockLessonRepository.getLessonsForStudent(any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
      onSuccess(lessons)
    }
    whenever(mockLessonRepository.deleteLesson(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful deletion
    }
    mockLessonViewModel.selectLesson(lessons[0])
  }

  @Test
  fun EditRequestedLessonIsProperlyDisplayed() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessonViewModel)
    }
    composeTestRule.onNodeWithTag("lessonContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
  }

  @Test
  fun sliderTest() {
    var changed = false
    composeTestRule.setContent { PriceRangeSlider("testLabel", { a, b -> changed = true }) }
    composeTestRule.onNodeWithTag("priceRangeSlider").performTouchInput { swipeRight() }
    assert(changed)
  }

  @Test
  fun validateValidatesValidly() {
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "time",
            1.0,
            1.0) == null)
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "",
            1.0,
            1.0) == "time is missing")
  }

  @Test
  fun confirmWithEmptyFieldsShowsToast() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessonViewModel)
    }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  @Test
  fun confirmWithValidFieldsNavigatesToHome() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessonViewModel)
    }

    // Fill in the required fields
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    // Select Date and Time (simulate selection)
    composeTestRule.onNodeWithTag("DateButton").performClick()
    // Assuming DatePickerDialog is shown, set selectedDate manually for test (mock behavior if
    // possible)

    composeTestRule.onNodeWithText("10/10/2024").assertExists()
    onView(withText("OK")).perform(click())

    composeTestRule.onNodeWithTag("TimeButton").performClick()
    // Assuming TimePickerDialog is shown, set selectedTime manually for test (mock behavior if
    // possible)
    composeTestRule.onNodeWithText("10:00").assertExists()
    onView(withText("OK")).perform(click())

    // Set Subject and Language
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdown${Subject.ANALYSIS}").performClick()
    composeTestRule.onNodeWithTag("languageSelectorRow").performClick()

    // Select location
    composeTestRule.onNodeWithTag("mapButton").performClick()
    composeTestRule.onNodeWithTag("map").performClick()
    Thread.sleep(2000) // Wait for the map to load
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.click(device.displayWidth / 2, device.displayHeight / 2)
    composeTestRule.onNodeWithTag("confirmLocation").performClick()

    // Confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions).navigateTo(anyString())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessonViewModel)
    }
    composeTestRule.onNodeWithText("10/10/2024").assertExists()
    composeTestRule.onNodeWithText("10:00").assertExists()
  }
}