package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.EnumSet

@RunWith(AndroidJUnit4::class)
class CreateTutorProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocking navigation actions
  private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)
  private val mockViewModel =
      Mockito.mock(ListProfilesViewModel::class.java).apply {
        Mockito.`when`(currentProfile).thenReturn(MutableStateFlow<Profile?>(null))
      }
  // list of languages
  private val languages = Language.entries.toTypedArray()
  private val subjects = Subject.entries.toTypedArray()

  @Test
  fun createTutorProfileScreen_rendersCorrectly() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("instructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutorInfoScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("subjectText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("priceText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("languageText").assertIsDisplayed()
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun selectingLanguagesUpdatesCorrectly() {

    val languages = mutableListOf<Language>()
    // Set the screen in the test environment
    composeTestRule.setContent { LanguageSelector(languages) }

    Language.entries.forEach { language ->
      // Check that the language is not selected
      composeTestRule.onNodeWithTag("checkbox_${language.name}").assertIsOff()

      // Select the language
      composeTestRule.onNodeWithTag("checkbox_${language.name}").performClick()

      assert(languages == mutableListOf(language))

      composeTestRule.onNodeWithTag("checkbox_${language.name}").performClick()

      assert(languages == mutableListOf<Language>())

      // Check that the language is now selected
      // composeTestRule.onNodeWithTag("checkbox_${language.name}").assertIsOn()
    }
  }

  @Test
  fun selectingSubjectsUpdatesCorrectly() {
    val subjects = mutableListOf<Subject>()
    composeTestRule.setContent { SubjectsSelector(subjects) }

    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").isNotDisplayed()
    }
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").assertIsDisplayed()
    }
  }

  @Test
  fun sliderTextTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2,
            languages = EnumSet.noneOf(Language::class.java),
            subjects = EnumSet.noneOf(Subject::class.java),
            schedule = listOf())
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("priceDifferenceLow").assertIsDisplayed()

    // Perform the sliding action on the slider
    composeTestRule.onNodeWithTag("priceSlider").performGesture { swipeRight() }

    // Verify the slider's value has changed
    composeTestRule.onNodeWithTag("priceDifferenceHigh").assertIsDisplayed()
  }

  @Test
  fun noProfileTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value = null
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateTutorProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory))
    }

    // Assert
    composeTestRule
        .onNodeWithTag("noProfile")
        .assertTextEquals("No Profile selected. Should not happen.")
  }
}
