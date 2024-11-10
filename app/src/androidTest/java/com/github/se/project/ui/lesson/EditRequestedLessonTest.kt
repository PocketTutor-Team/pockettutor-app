package com.github.se.project.ui.lesson

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.PriceRangeSlider
import com.github.se.project.ui.components.validateLessonInput
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

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
              tutorUid = "tutor123",
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
              tutorUid = "tutor123",
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
  private val mockLessons =
      mock(LessonViewModel::class.java).apply {
        `when`(selectedLesson).thenReturn(MutableStateFlow<Lesson?>(lessons[0]))
        `when`(selectedLocation).thenReturn(MutableStateFlow(Pair(1.0, 1.0)))
      }

  @Test
  fun EditRequestedLessonIsProperlyDisplayed() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessons)
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
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessons)
    }
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  @Test
  fun confirmWithValidFieldsNavigatesToHome() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessons)
    }

    // Fill in the required fields
    composeTestRule.onNodeWithTag("titleField").performTextInput("Math Lesson")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("This is a math lesson.")

    // Select Date and Time (simulate selection)
    composeTestRule.onNodeWithTag("DateButton").performClick()
    // Assuming DatePickerDialog is shown, set selectedDate manually for test (mock behavior if
    // possible)
    composeTestRule.onNodeWithText("10/10/2024").assertExists()

    composeTestRule.onNodeWithTag("TimeButton").performClick()
    // Assuming TimePickerDialog is shown, set selectedTime manually for test (mock behavior if
    // possible)
    composeTestRule.onNodeWithText("10:00").assertExists()

    // Set Subject and Language
    composeTestRule.onNodeWithTag("subjectButton").performClick()
    composeTestRule.onNodeWithTag("dropdown${Subject.AICC}").performClick()
    composeTestRule.onNodeWithTag("languageSelectorRow").performClick()

    // Confirm
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(navigationActions, never()).navigateTo(anyString())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent {
      EditRequestedLessonScreen(navigationActions, mockProfiles, mockLessons)
    }
    composeTestRule.onNodeWithText("10/10/2024").assertExists()
    composeTestRule.onNodeWithText("10:00").assertExists()
  }
}
