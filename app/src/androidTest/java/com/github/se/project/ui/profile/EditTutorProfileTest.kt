package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import java.util.EnumSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.doReturn

@RunWith(AndroidJUnit4::class)
class EditTutorProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mutableStateFlow: MutableStateFlow<Profile?>
  private lateinit var mockViewModel: ListProfilesViewModel
  private lateinit var mockNavigationActions: NavigationActions

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
  fun editTutorProfileScreen_rendersCorrectly() {
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
    composeTestRule.setContent { EditTutorProfile(mockNavigationActions, mockViewModel) }

    // Assert all expected UI components are visible
    // composeTestRule.onNodeWithTag("editTutorProfileCloseButton").assertExists()
    composeTestRule.onNodeWithTag("editTutorWelcomeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileInstructionText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileSectionDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfileAcademicLevelDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("editTutorProfilePriceText").assertIsDisplayed()
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
            languages = EnumSet.of(Language.ENGLISH),
            subjects = EnumSet.of(Subject.ALGEBRA),
            schedule = List(7) { List(12) { 0 } })
    // Set the screen in the test environment
    composeTestRule.setContent { EditTutorProfile(mockNavigationActions, mockViewModel) }

    composeTestRule.onNodeWithTag("editTutorProfileAcademicLevelDropdown").performClick()
    composeTestRule
        .onNodeWithTag("editTutorProfileAcademicLevelDropdownItem-MA4")
        .assertIsDisplayed()
        .performClick()

    composeTestRule.onNodeWithTag("editTutorProfileSectionDropdown").performClick()
    composeTestRule
        .onNodeWithTag("editTutorProfileSectionDropdownItem-GM")
        .assertIsDisplayed()
        .performClick()

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
            languages = EnumSet.of(Language.ENGLISH),
            subjects = EnumSet.of(Subject.ALGEBRA),
            schedule = List(7) { List(12) { 0 } })
    // Set the screen in the test environment
    composeTestRule.setContent { EditTutorProfile(mockNavigationActions, mockViewModel) }

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
      EditTutorProfile(navigationActions = mockNavigationActions, mockViewModel)
    }
    composeTestRule.onNodeWithTag("editTutorProfileConfirmButton").performClick()

    Mockito.verify(mockNavigationActions, Mockito.never()).navigateTo(Mockito.anyString())
  }

  @Test
  fun noEditProfileTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value = null
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditTutorProfile(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory))
    }

    composeTestRule
        .onNodeWithTag("editTutorNoProfile")
        .assertTextEquals("No Profile selected. Should not happen.")
  }
}
