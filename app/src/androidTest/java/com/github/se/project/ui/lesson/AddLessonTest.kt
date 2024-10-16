package com.github.se.project.ui.lesson

import androidx.compose.runtime.mutableStateOf
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
import com.github.se.project.ui.components.PriceRangeSlider
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
    composeTestRule.setContent { PriceRangeSlider("testLabel") { a, b -> changed = true } }

    // Perform the sliding action on the slider
    composeTestRule.onNodeWithTag("priceRangeSlider").performTouchInput { swipeRight() }

    // Verify the slider's value has changed
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
            "time") == null)
    assert(
        validateLessonInput(
            "title",
            "description",
            mutableStateOf(Subject.AICC),
            listOf(Language.ENGLISH),
            "date",
            "") == "time is missing")
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
}
