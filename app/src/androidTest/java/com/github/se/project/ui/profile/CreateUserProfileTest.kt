package com.github.se.project.ui.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.EpflVerificationRepository
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.SectionSelector
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

  private val certificationRepository = Mockito.mock(EpflVerificationRepository::class.java)

  private val certificationViewModel =
      CertificationViewModel(certificationRepository, mockViewModel)

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
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Assert all expected UI components are visible
    composeTestRule.onNodeWithTag("welcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("firstNameField").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameField").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleSelection").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsDisplayed()
    composeTestRule.onNodeWithTag("phoneNumberField").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performScrollTo()
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().assertIsDisplayed()
  }

  @Test
  fun formValidation_displaysToastWhenFieldsAreEmpty() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Click on the Confirm button with empty fields
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Ensure that the navigation has not been triggered
    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun phoneNumberValidation_showsErrorForInvalidPhone() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Enter an invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performScrollTo().performTextInput("123ABC")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify no navigation is triggered due to invalid phone number
    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun selectingRole_updatesRoleCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Select "Student" role
    composeTestRule.onNodeWithTag("roleButtonStudent").performScrollTo().performClick()

    // Check that the "Student" button is selected
    composeTestRule.onNodeWithTag("roleButtonStudent").performScrollTo().assertIsSelected()

    // Select "Tutor" role
    composeTestRule.onNodeWithTag("roleButtonTutor").performScrollTo().performClick()

    // Check that the "Tutor" button is now selected
    composeTestRule.onNodeWithTag("roleButtonTutor").performScrollTo().assertIsSelected()
  }

  @Test
  fun roleSwitch_updatesSelectionCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Click on the Tutor button
    composeTestRule.onNodeWithTag("roleButtonTutor").performScrollTo().performClick()

    // Ensure "Tutor" role is selected and "Student" role is not selected
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsNotSelected()
  }

  @Test
  fun selectingSection_updatesSectionCorrectly() {
    val section: MutableState<Section?> = mutableStateOf(null)
    composeTestRule.setContent { SectionSelector(section) }

    // Open section dropdown
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()

    // Select a section from the dropdown
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()

    // Check that the section is updated correctly
    composeTestRule.onNodeWithTag("sectionDropdown").assertTextEquals("SC")
    assert(section.value == Section.SC)
  }

  @Test
  fun selectingAcademicLevel_updatesAcademicLevelCorrectly() {
    val academicLevel: MutableState<AcademicLevel?> = mutableStateOf(null)
    composeTestRule.setContent { AcademicSelector(academicLevel) }

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
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Enter invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123")

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify no navigation happens due to invalid phone number
    verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun emptyFields_preventFormSubmission() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Leave fields empty and click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify that no navigation occurs due to empty fields
    verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun validFormSubmission_navigatesToCorrectScreen() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Enter valid data for all fields
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")

    // Select country code
    composeTestRule.onNodeWithTag("countryCodeField").performScrollTo().performClick()
    // Wait for the dropdown to appear
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("country_plus41").performScrollTo().performClick()

    // Enter phone number without country code
    composeTestRule.onNodeWithTag("phoneNumberField").performScrollTo().performTextInput("34567890")

    // Select role as Tutor
    composeTestRule.onNodeWithTag("roleButtonTutor").performScrollTo().performClick()

    // Select section and academic level
    composeTestRule.onNodeWithTag("sectionDropdown").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA6").performScrollTo().performClick()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo()
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Verify that navigation occurred
    verify(mockNavigationActions).navigateTo(Screen.CREATE_TUTOR_PROFILE)
  }

  @Test
  fun phoneNumberValidation_showsErrorForPhoneWithSpecialCharacters() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Enter a phone number with special characters
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123-456-7890")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify no navigation is triggered due to invalid phone number
    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun formValidation_displaysErrorWhenSectionAndAcademicLevelNotSelected() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun selectingAndChangingSection_updatesSectionCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Open section dropdown and select a section
    composeTestRule.onNodeWithTag("sectionDropdown").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").performScrollTo().assertTextEquals("SC")

    // Change the section
    composeTestRule.onNodeWithTag("sectionDropdown").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-IN").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("sectionDropdown").assertTextEquals("IN")
  }

  @Test
  fun selectingAndChangingRole_updatesRoleCorrectly() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Select the Student role
    composeTestRule.onNodeWithTag("roleButtonStudent").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("roleButtonStudent").assertIsSelected()

    // Change the role to Tutor
    composeTestRule.onNodeWithTag("roleButtonTutor").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("roleButtonTutor").assertIsSelected()

    // Verify Student is no longer selected
    composeTestRule.onNodeWithTag("roleButtonStudent").performScrollTo().assertIsNotSelected()
  }

  @Test
  fun formValidation_displaysErrorWhenRoleNotSelected() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")

    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun emptyPhoneNumber_preventsFormSubmission() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("John")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("roleButtonTutor").performClick()

    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    verify(mockNavigationActions, Mockito.never()).navigateTo(anyString())
  }

  @Test
  fun testProfileCreationIncompleteInput() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
    }

    // Input first name only, leave other fields empty
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Alice")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Doe")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0123456789")
    // Click the create profile button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify that the navigation action was not called
    verify(mockNavigationActions, Mockito.never()).navigateTo(Screen.HOME)
  }

  @Test
  fun testProfileCreationInvalidPhoneNumber() {
    composeTestRule.setContent {
      CreateProfileScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockViewModel,
          certificationViewModel = certificationViewModel,
          testMode = true)
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
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    // Verify that the navigation action was not called
    verify(mockNavigationActions, Mockito.never()).navigateTo(Screen.HOME)
  }
}
