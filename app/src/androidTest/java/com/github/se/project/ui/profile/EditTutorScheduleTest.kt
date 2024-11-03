package com.github.se.project.ui.profile

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.theme.SampleAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.doReturn

class EditTutorScheduleTest {
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
  fun editSchedule_displaysProfileNameAndInstructions() {
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

    composeTestRule.setContent {
      SampleAppTheme { EditTutorSchedule(mockNavigationActions, mockViewModel) }
    }

    // Verify that the greeting text is displayed correctly
    composeTestRule
        .onNodeWithTag("editScheduleWelcomeText")
        .assertTextEquals("John, show us your availabilities")

    // Verify that the instructions text is displayed correctly
    composeTestRule
        .onNodeWithTag("editScheduleInstructionsText")
        .assertTextEquals("Modify the time slots you're available during the week:")

    // Verify that the "Let's find a student!" button is displayed and has the correct text
    composeTestRule.onNodeWithTag("editScheduleButton").assertTextEquals("Update Schedule")
    composeTestRule.onNodeWithTag("editScheduleButton").performClick()

    // Verify that the updateProfile was called
    // assertEquals(AcademicLevel.MA4, mockViewModel.currentProfile.value?.academicLevel)
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
    composeTestRule.setContent { EditTutorSchedule(mockNavigationActions, mockViewModel) }

    composeTestRule.onNodeWithTag("editScheduleCloseButton").performClick()
    Mockito.verify(mockNavigationActions).goBack()
  }

  @Test
  fun noEditProfileTest() {
    (mockViewModel.currentProfile as MutableStateFlow).value = null
    // Set the screen in the test environment
    composeTestRule.setContent {
      EditTutorSchedule(mockNavigationActions, viewModel(factory = ListProfilesViewModel.Factory))
    }

    composeTestRule
        .onNodeWithTag("editScheduleNoProfile")
        .assertTextEquals("No Profile selected. Should not happen.")
  }
}
