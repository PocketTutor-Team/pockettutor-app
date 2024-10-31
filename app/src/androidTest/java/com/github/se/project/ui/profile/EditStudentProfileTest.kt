package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EditStudentProfileTest {
  private lateinit var mockProfilesRepository: ProfilesRepository
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockListProfilesViewModel: ListProfilesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  private val mockStudentProfile =
      Profile(
          uid = "1",
          googleUid = "1",
          firstName = "Student",
          lastName = "Test",
          phoneNumber = "1234567890",
          role = Role.STUDENT,
          section = Section.GM,
          academicLevel = AcademicLevel.MA2)

  @Before
  fun setUp() {
    mockNavigationActions = Mockito.mock(NavigationActions::class.java)
    mockProfilesRepository = Mockito.mock(ProfilesRepository::class.java)
    mockListProfilesViewModel = ListProfilesViewModel(mockProfilesRepository)
    mockListProfilesViewModel = spy(mockListProfilesViewModel)
    doNothing().`when`(mockProfilesRepository).init(any())
    whenever(mockProfilesRepository.updateProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    val currentProfileFlow = MutableStateFlow<Profile?>(mockStudentProfile)
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile
  }

  @Test
  fun editStudentProfileNoProfile() {
    val currentProfileFlow = MutableStateFlow<Profile?>(null)
    doReturn(currentProfileFlow).`when`(mockListProfilesViewModel).currentProfile
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }
    composeTestRule.onNodeWithTag("editTutorNoProfile").assertIsDisplayed()
  }

  @Test
  fun editStudentProfile_rendersCorrectly() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }

    // Assert all expected UI components are visible
    composeTestRule.onNodeWithTag("lastNameField").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("firstNameField").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("editProfileCloseButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nameTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editNameButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileInstructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileSectionDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editProfileAcademicLevelDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").assertIsDisplayed()
  }

  @Test
  fun editNameButton() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }
    composeTestRule.onNodeWithTag("editNameButton").performClick()
    composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameField").assertIsDisplayed()

    composeTestRule.onNodeWithTag("firstNameField").performTextInput("New")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("New")

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assert(mockStudentProfile.firstName == "NewStudent")
    assert(mockStudentProfile.lastName == "NewTest")

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun nameDelete() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }
    composeTestRule.onNodeWithTag("editNameButton").performClick()
    composeTestRule.onNodeWithTag("firstNameField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lastNameField").assertIsDisplayed()

    composeTestRule.onNodeWithTag("firstNameField").performClick()
    composeTestRule.onNodeWithTag("firstNameField").performTextClearance()

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assert(mockStudentProfile.lastName == "Test")

    verify(mockNavigationActions, never()).goBack()
  }

  @Test
  fun phoneNumberValid() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }

    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("00")

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assert(mockStudentProfile.phoneNumber == "001234567890")

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun phoneNumberInvalid() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }

    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("abc")

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assert(mockStudentProfile.phoneNumber == "1234567890")

    verify(mockNavigationActions, never()).goBack()
  }

  @Test
  fun sectionAndLevel() {
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditStudentProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel)
    }

    composeTestRule.onNodeWithTag("editProfileAcademicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("editTutorProfileAcademicLevelDropdownItem-MA1").performClick()

    composeTestRule.onNodeWithTag("editProfileSectionDropdown").performClick()
    composeTestRule.onNodeWithTag("editProfileSectionDropdownItem-IN").performClick()

    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    assert(mockStudentProfile.section == Section.IN)
    assert(mockStudentProfile.academicLevel == AcademicLevel.MA1)

    verify(mockNavigationActions).goBack()
  }
}
