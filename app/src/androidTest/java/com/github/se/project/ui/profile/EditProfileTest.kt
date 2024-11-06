package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class EditProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mutableStateFlow: MutableStateFlow<Profile?>
  private lateinit var mockViewModel: ListProfilesViewModel
  private lateinit var mockNavigationActions: NavigationActions

  private var profile =
      Profile(
          uid = "1",
          googleUid = "googleUid",
          firstName = "First",
          lastName = "Last",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2,
          languages = listOf(Language.ENGLISH),
          subjects = listOf(Subject.ALGEBRA),
          schedule = listOf())

  @Before
  fun setUp() {
    mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    mutableStateFlow = MutableStateFlow(null)

    val mockProfilesRepository = Mockito.mock(ProfilesRepository::class.java)

    // If using spy, use doReturn
    mockViewModel = Mockito.spy(ListProfilesViewModel(mockProfilesRepository))
    doReturn(mutableStateFlow as StateFlow<Profile?>).`when`(mockViewModel).currentProfile
  }


  @Test
  fun tutorFieldsDontShowForStudentAccount() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "1",
            googleUid = "googleUid",
            firstName = "First",
            lastName = "Last",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.GM,
            academicLevel = AcademicLevel.MA2)
    // Set the screen in the test environment
    composeTestRule.setContent { EditProfile(mockNavigationActions, mockViewModel) }

    composeTestRule.onNodeWithTag("lastNameField").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("firstNameField").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileInstructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("phoneNumberField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("sectionDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("academicLevelDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileLanguageText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileSubjectText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfilePriceText").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").assertIsDisplayed()
  }

  @Test
  fun editTutorProfileUpdatesCorrectly() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "12345",
            googleUid = "67890",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            role = Role.TUTOR,
            section = Section.IN,
            academicLevel = AcademicLevel.MA2,
            languages = listOf(Language.ENGLISH),
            subjects = listOf(Subject.ALGEBRA),
            schedule = List(7) { List(12) { 0 } })
    // Set the screen in the test environment
    composeTestRule.setContent { EditProfile(mockNavigationActions, mockViewModel) }

    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule
        .onNodeWithTag("academicLevelDropdownItem-MA4")
        .assertIsDisplayed()
        .performClick()

    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-GM").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assertEquals(AcademicLevel.MA4, mockViewModel.currentProfile.value?.academicLevel)
    assertEquals(Section.GM, mockViewModel.currentProfile.value?.section)
  }

  @Test
  fun editTutorClosePage() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "12345",
            googleUid = "67890",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            role = Role.TUTOR,
            section = Section.IN,
            academicLevel = AcademicLevel.MA2,
            languages = listOf(Language.ENGLISH),
            subjects = listOf(Subject.ALGEBRA),
            schedule = List(7) { List(12) { 0 } })
    // Set the screen in the test environment
    composeTestRule.setContent { EditProfile(mockNavigationActions, mockViewModel) }

    composeTestRule.onNodeWithTag("editTutorProfileCloseButton").performClick()
    Mockito.verify(mockNavigationActions).goBack()
  }

  @Test
  fun editTutorProfileScreen_ToastMessage() {
    (mockViewModel.currentProfile as MutableStateFlow).value =
        Profile(
            uid = "12345",
            googleUid = "67890",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            role = Role.TUTOR,
            section = Section.IN,
            academicLevel = AcademicLevel.MA2,
            schedule = List(7) { List(12) { 0 } })
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditProfile(navigationActions = mockNavigationActions, mockViewModel)
    }
    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    Mockito.verify(mockNavigationActions, never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun phoneNumberValidation_showsErrorForInvalidPhone() {
    (mockViewModel.currentProfile as MutableStateFlow).value = profile
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    // Enter an invalid phone number
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("123ABC")

    // Click on the Confirm button
    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    // Verify no navigation is triggered due to invalid phone number
    Mockito.verify(mockNavigationActions, never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun phoneNumberValid() {
    // Set the screen in the test environment
    (mockViewModel.currentProfile as MutableStateFlow).value = profile
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditProfile(navigationActions = mockNavigationActions, mockViewModel)
    }

    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("00")

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assertEquals("001234567890", mockViewModel.currentProfile.value?.phoneNumber)

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun noEditProfileTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value = null
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory))
    }

    composeTestRule
        .onNodeWithTag("editTutorNoProfile")
        .assertTextEquals("No Profile selected. Should not happen.")
  }
}
