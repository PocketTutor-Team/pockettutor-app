package com.github.se.project.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class CreateProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock du NavigationActions
  private val mockNavigationActions = Mockito.mock(NavigationActions::class.java)

  // Mock du ProfilesRepository
  private val mockProfilesRepository = Mockito.mock(ProfilesRepository::class.java)

  // Mock du ViewModel avec le ProfilesRepository mocké
  private val mockViewModel = ListProfilesViewModel(mockProfilesRepository)

  @Before
  fun setUp() {
    // Stubbing du getNewUid pour retourner une valeur factice
    Mockito.`when`(mockProfilesRepository.getNewUid()).thenReturn("mockUid")
  }

  @Test
  fun createProfileScreen_rendersCorrectly() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Assert all expected UI components are visible
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("instructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleSelection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sectionDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("academicLevelDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("phoneNumberField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Click on the Confirm button with empty fields
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Ensure that the navigation has not been triggered
    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun phoneNumberValidation_showsErrorForInvalidPhone() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Enter an invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123ABC")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify no navigation is triggered due to invalid phone number
    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun selectingRole_updatesRoleCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Select "Student" role
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

    // Check that the "Student" button is selected
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsSelected()

    // Select "Tutor" role
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    // Check that the "Tutor" button is now selected
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()
  }

  @Test
  fun roleSwitch_updatesSelectionCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Click on the Tutor button
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    // Ensure "Tutor" role is selected and "Student" role is not selected
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsNotSelected()
  }

  @Test
  fun selectingSection_updatesSectionCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Open section dropdown
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()

    // Select a section from the dropdown
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()

    // Check that the section is updated correctly
    composeTestRule.onNodeWithTag("sectionDropdown").assertTextEquals("SC")
  }

  @Test
  fun selectingAcademicLevel_updatesAcademicLevelCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Open academic level dropdown
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()

    // Select an academic level from the dropdown
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

    // Check that the academic level is updated correctly
    composeTestRule.onNodeWithTag("academicLevelDropdown").assertTextEquals("BA3")
  }

  @Test
  fun invalidPhoneNumber_preventsNavigation() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Enter invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123")

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify no navigation happens due to invalid phone number
    verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun emptyFields_preventFormSubmission() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Leave fields empty and click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify that no navigation occurs due to empty fields
    verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun validFormSubmission_navigatesToCorrectScreen() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockGoogleUid",
          listProfilesViewModel = mockViewModel)
    }

    // Enter valid data for all fields
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("+1234567890")
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    // Select section and academic level
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Vérifier que la navigation a bien eu lieu
    verify(mockNavigationActions).navigateTo(Screen.CREATE_TUTOR_PROFILE)
  }

  @Test
  fun phoneNumberValidation_showsErrorForPhoneWithSpecialCharacters() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Enter a phone number with special characters
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123-456-7890")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify no navigation is triggered due to invalid phone number
    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun formValidation_displaysErrorWhenSectionAndAcademicLevelNotSelected() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun selectingAndChangingSection_updatesSectionCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Open section dropdown and select a section
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").assertTextEquals("SC")

    // Change the section
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-IN").performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").assertTextEquals("IN")
  }

  @Test
  fun selectingAndChangingRole_updatesRoleCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    // Select the Student role
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsSelected()

    // Change the role to Tutor
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()

    // Verify Student is no longer selected
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsNotSelected()
  }

  @Test
  fun formValidation_displaysErrorWhenRoleNotSelected() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun emptyPhoneNumber_preventsFormSubmission() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          googleUid = "mockUid",
          listProfilesViewModel = mockViewModel)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    composeTestRule.onNodeWithTag("confirmButton").performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun testProfileCreationIncompleteInput() {
    composeTestRule.setContent {
      CreateProfileScreen(mockNavigationActions, mockViewModel, googleUid = "test_google_uid")
    }

    // Input first name only, leave other fields empty
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Alice")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0123456789")
    // Click the create profile button
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule
        .onNodeWithTag("errorText")
        .assertTextEquals("Please complete all the fields before creating your account!")

    // Verify that the navigation action was not called
    verify(mockNavigationActions, Mockito.never()).navigateTo(Screen.HOME)
  }

  @Test
  fun testProfileCreationInvalidPhoneNumber() {
    composeTestRule.setContent {
      CreateProfileScreen(mockNavigationActions, mockViewModel, googleUid = "test_google_uid")
    }

    // Input valid first name, last name, and invalid phone number
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Jane")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123ABC")
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA6").performClick()

    // Click the create profile button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    composeTestRule
        .onNodeWithTag("errorText")
        .assertTextEquals("Please enter a valid phone number!")

    // Verify that the navigation action was not called
    verify(mockNavigationActions, Mockito.never()).navigateTo(Screen.HOME)
  }
}
