package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class CreateTutorProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocking navigation actions
  private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)
  private val mockProfileRepository = Mockito.mock(ProfilesRepository::class.java)
  private val mockViewModel = ListProfilesViewModel(mockProfileRepository)

  // Helper function to create a mock Profile
  private fun getMockProfile() =
      Profile(
          uid = "1",
          token = "",
          googleUid = "googleUid",
          firstName = "First",
          lastName = "Last",
          phoneNumber = "1234567890",
          role = Role.TUTOR,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(),
          subjects = listOf(),
          schedule = listOf())

  @Before
  fun setUp() {
    `when`(mockProfileRepository.getNewUid()).thenReturn("uid")
    `when`(mockProfileRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf())
    }
  }

  @Test
  fun createTutorProfileScreen_rendersCorrectly() {
    mockViewModel.setCurrentProfile(getMockProfile())

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
    composeTestRule.onNodeWithTag("experienceField").assertIsDisplayed()
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    mockViewModel.setCurrentProfile(getMockProfile())

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
    composeTestRule.setContent { SubjectSelector(null, subjects, true) }

    Subject.entries.forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").isNotDisplayed()
    }
    composeTestRule.onNodeWithTag("subjectButton").performClick()

    Subject.entries.take(4).forEach { subject ->
      if (subject == Subject.NONE) return@forEach
      composeTestRule.onNodeWithTag("dropdown${subject.name}").assertIsDisplayed()
    }
  }

  @Test
  fun sliderTextTest() {
    mockViewModel.setCurrentProfile(getMockProfile())

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
    mockViewModel.setCurrentProfile(null)

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

  @Test
  fun descriptionField_updatesCorrectly() {
    mockViewModel.setCurrentProfile(getMockProfile())

    composeTestRule.setContent {
      CreateTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule
        .onNodeWithTag("experienceField")
        .assertTextContains("Do you have any experience as a tutor ?")

    val descriptionText = "I have experience teaching math and physics."
    composeTestRule.onNodeWithTag("experienceField").performTextInput(descriptionText)

    composeTestRule.onNodeWithTag("experienceField").assertTextContains(descriptionText)
  }
}
