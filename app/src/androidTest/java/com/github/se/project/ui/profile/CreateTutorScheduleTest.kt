package com.github.se.project.ui.profile

/*
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.profile.CreateTutorSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class CreateTutorScheduleTest {
  private lateinit var profilesRepository: ProfilesRepository
  private lateinit var navigationActions: NavigationActions
  private lateinit var listProfilesViewModel: ListProfilesViewModel

  // Mock profile data for testing
  private val mockProfile =
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
  private val mockProfileFlow = MutableStateFlow<Profile?>(mockProfile)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the dependencies
    profilesRepository = mock(ProfilesRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)


    // Create a real instance of ListProfilesViewModel
    listProfilesViewModel = ListProfilesViewModel(profilesRepository)

    // Mock the currentProfile StateFlow to return the mockProfile
    val currentProfileFlow = MutableStateFlow<Profile?>(mockProfile)

    // Use a Mockito spy to track the actual state of the view model
    listProfilesViewModel = spy(listProfilesViewModel)

    // Stubbing the StateFlow
    doReturn(currentProfileFlow).`when`(listProfilesViewModel).currentProfile

    // Stub the updateProfile method to simulate a successful update
    doNothing().`when`(profilesRepository).init(any())
    whenever(profilesRepository.updateProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
  }

  @Test
  fun availabilityScreen_displaysProfileNameAndInstructions() {
    composeTestRule.setContent { CreateTutorSchedule(navigationActions, listProfilesViewModel) }

    // Verify that the greeting text is displayed correctly
    composeTestRule
        .onNodeWithTag("welcomeText")
        .assertTextEquals("John, show us your availabilities")

    // Verify that the instructions text is displayed correctly
    composeTestRule
        .onNodeWithTag("InstructionsText")
        .assertTextEquals(
            "Finish your account creation by selecting the time slots you're available during the week:")

    // Verify that the "Let's find a student!" button is displayed and has the correct text
    composeTestRule.onNodeWithTag("FindStudentButton").assertTextEquals("Let's find a student!")
    composeTestRule.onNodeWithTag("FindStudentButton").performClick()

    // Verify that the updateProfile was called
    verify(profilesRepository).updateProfile(any(), any(), any())
  }
}
*/
