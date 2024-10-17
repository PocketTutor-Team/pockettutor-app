package com.github.se.project.ui.lesson

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import java.util.EnumSet
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class AddLessonTest {

  @get:Rule val composeTestRule = createComposeRule()

  val navigationActions = Mockito.mock(NavigationActions::class.java)
  val profile =
      Profile(
          "uid",
          "googleUid",
          "firstName",
          "lastName",
          "phoneNumber",
          Role.TUTOR,
          Section.AR,
          AcademicLevel.BA1,
          EnumSet.of(Language.ENGLISH),
          EnumSet.of(Subject.ANALYSIS),
          List(7) { List(12) { 0 } },
          0)
  val mockProfiles =
      Mockito.mock(ListProfilesViewModel::class.java).apply {
        Mockito.`when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(profile))
      }
  val mockLessons = Mockito.mock(LessonViewModel::class.java)

  @Test
  fun AddLessonIsProperlyDisplayed() {

    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }

    composeTestRule.onNodeWithTag("bigColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("titleField").assertIsDisplayed()
  }

  @Test
  fun sliderTest() {
    // Set the screen in the test environment
    var changed = false
    composeTestRule.setContent { PriceSlider("testLabel") { a, b -> changed = true } }

    // Perform the sliding action on the slider
    composeTestRule.onNodeWithTag("priceSlider").performTouchInput { swipeRight() }

    // Verify the slider's value has changed
    assert(changed)
  }

  @Test
  fun validateValidatesValidly() {
    assert(validateLessonInput("title", "description", "AICC", "language", "date", "time") == null)
    assert(
        validateLessonInput("title", "description", "AICC", "language", "date", "") ==
            "time is missing")
    assert(
        validateLessonInput("title", "description", "subject", "language", "", "time") ==
            "date is missing")
    assert(
        validateLessonInput("title", "description", "subject", "language", "date", "time") ==
            "Invalid subject")
  }

  @Test
  fun confirm() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }

    composeTestRule.onNodeWithTag("confirmButton").performClick()
    Mockito.verify(navigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun testInitialState() {
    composeTestRule.setContent { AddLessonScreen(navigationActions, mockProfiles, mockLessons) }

    // Check initial state of date and time selection
    composeTestRule.onNodeWithText("Select Date").assertExists()
    composeTestRule.onNodeWithText("Select Time").assertExists()
  }

  /*@Test
  fun testSuccessfulDateSelection() {
      // Set up the initial state
      val currentDate = Calendar.getInstance().apply {
          set(Calendar.YEAR, 2023)
          set(Calendar.MONTH, Calendar.OCTOBER)
          set(Calendar.DAY_OF_MONTH, 17)
      }

      composeTestRule.setContent {
          // Use a mock NavigationActions and ViewModels as needed
          AddLessonScreen(navigationActions, mockProfiles, mockLessons)
      }

      // Show the DatePickerDialog and select a future date
      composeTestRule.onNodeWithText("Select Date").performClick()

      // Assuming the DatePickerDialog is opened and you can interact with it
      composeTestRule.onAllNodesWithText("17").onFirst().performClick() // Day
      composeTestRule.onAllNodesWithText("10").onFirst().performClick() // Month
      composeTestRule.onAllNodesWithText("2024").onFirst().performClick() // Year

      // Assert that the selected date is now visible in the UI
      composeTestRule.onNodeWithText("Selected Date: 17/10/2024").assertIsDisplayed()
  }

  @Test
  fun testInvalidPastDateSelection() {
      // Set up the initial state
      val currentDate = Calendar.getInstance().apply {
          set(Calendar.YEAR, 2023)
          set(Calendar.MONTH, Calendar.OCTOBER)
          set(Calendar.DAY_OF_MONTH, 17)
      }

      composeTestRule.setContent {
          // Use a mock NavigationActions and ViewModels as needed
          AddLessonScreen(navigationActions, mockProfiles, mockLessons)
      }

      // Show the DatePickerDialog and select a past date
      composeTestRule.onNodeWithText("Select Date").performClick()

      // Assuming the DatePickerDialog is opened and you can interact with it
      composeTestRule.onAllNodesWithText("16").onFirst().performClick() // Day
      composeTestRule.onAllNodesWithText("10").onFirst().performClick() // Month
      composeTestRule.onAllNodesWithText("2023").onFirst().performClick() // Year

      // Assert that a toast message indicating the error is displayed
      composeTestRule.onNodeWithText("You cannot select a past date", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testTimePickerShowsCorrectTime() {
      composeTestRule.setContent {
          AddLessonScreen(navigationActions, mockProfiles, mockLessons)
      }

      // Open the time picker
      composeTestRule.onNodeWithText("Select Time").performClick()

      // Simulate selecting a time
      val hour = 14 // 2 PM
      val minute = 30

      // Assuming you have a method to trigger the TimePickerDialog directly
      composeTestRule.onNodeWithText("OK").performClick() // Simulate the OK button click

      // Check if the selected time is displayed correctly
      composeTestRule.onNodeWithText("Selected Time: 14:30").assertExists()
  }*/
}
