package com.github.se.project.ui.endToEnd

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.se.project.PocketTutorApp
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonRepository
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.ProfilesRepository
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.lesson.assertEnabledToBoolean
import com.github.se.project.ui.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class EndToEndStudentTest {

  @Mock lateinit var navigationActions: NavigationActions

  private val context = ApplicationProvider.getApplicationContext<Context>()

  // Mock du ProfilesRepository
  private val mockProfileRepository = mock(ProfilesRepository::class.java)

  private val mockProfileViewModel = ListProfilesViewModel(mockProfileRepository)

  private val mockLessonRepository = mock(LessonRepository::class.java)

  private val mockLessonViewModel = LessonViewModel(mockLessonRepository)

  private val mockIsConnected = MutableStateFlow(true)
  private lateinit var networkStatusViewModel: NetworkStatusViewModel
  var testMapReady = false

  private val mockTutor =
      Profile(
          "mockTutor",
          "",
          "mockTutor",
          "Ozymandias",
          "Halifax",
          "1234567890",
          Role.TUTOR,
          Section.IN,
          AcademicLevel.BA3,
          listOf(),
          "I have experience teaching math and physics.",
          listOf(Language.ENGLISH),
          listOf(Subject.AICC),
          List(7) { List(12) { 1 } },
          5)

  private var currentLesson: Lesson? = null

  // Mock ChatViewModel
  // val mockChatClient = mock(ChatClient::class.java) // not used for now, will be used in a
  // following PR
  private val mockChatViewModel: ChatViewModel = mock(ChatViewModel::class.java)

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val grantNotificationPermission: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    whenever(mockProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockProfileRepository.getProfiles(any(), any())).thenAnswer { invocation ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[0] as (List<Profile>) -> Unit
      onSuccess(listOf(mockTutor)) // Simulate a list of profiles with our beloved Ozymandias
    }
    whenever(mockProfileRepository.getNewUid()).thenReturn("mockUid")
    whenever(mockLessonRepository.getNewUid()).thenReturn("mockUid")
    whenever(mockLessonRepository.addLesson(any(), any(), any())).thenAnswer { invocation ->
      currentLesson = invocation.arguments[0] as Lesson
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockLessonRepository.updateLesson(any(), any(), any())).thenAnswer { invocation ->
      currentLesson = invocation.arguments[0] as Lesson
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful update
    }
    whenever(mockLessonRepository.getLessonsForStudent(any(), any(), any())).thenAnswer { invocation
      ->
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as (List<Lesson>) -> Unit
      if (currentLesson == null) {
        onSuccess(emptyList()) // Simulate an empty list of lessons
      } else {
        onSuccess(listOf(currentLesson!!))
      } // Simulate a list of lessons with our current lesson
    }
    whenever(mockLessonRepository.deleteLesson(any(), any(), any())).thenAnswer { invocation ->
      currentLesson = null
      @Suppress("UNCHECKED_CAST") val onSuccess = invocation.arguments[1] as () -> Unit
      onSuccess() // Simulate a successful deletion
    }

    networkStatusViewModel =
        object : NetworkStatusViewModel(application = ApplicationProvider.getApplicationContext()) {
          override val isConnected = mockIsConnected
        }

    // Start the app in test mode
    composeTestRule.setContent {
      PocketTutorApp(
          true,
          viewModel(),
          mockProfileViewModel,
          mockLessonViewModel,
          networkStatusViewModel,
          onMapReadyChange = { testMapReady = it },
          chatViewModel = mockChatViewModel)
    }
  }

  // The test interacts with the UI components to simulate the entire user journey, from logging in
  // and editing profile information, to creating and scheduling a lesson. All 5 types of lessons
  // displayed to students are tested: requesting a specific tutor, creating an open request, having
  // a tutor respond to your request, having a confirmed lesson, and having a completed lesson.
  @Test
  fun endToEndStudentCreateAccountAndLesson() {
    composeTestRule.waitForIdle()

    // Sign in
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // Enter valid data for all fields
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Alice")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Dupont")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo()
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

    // Select section and academic level
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    composeTestRule.waitForIdle()

    assert(mockProfileViewModel.currentProfile.value != null)
    assert(mockProfileViewModel.currentProfile.value!!.firstName == "Alice")
    assert(mockProfileViewModel.currentProfile.value!!.lastName == "Dupont")
    assert(mockProfileViewModel.currentProfile.value!!.phoneNumber == "+411234567890")
    assert(mockProfileViewModel.currentProfile.value!!.role == Role.STUDENT)
    assert(mockProfileViewModel.currentProfile.value!!.section == Section.SC)
    assert(mockProfileViewModel.currentProfile.value!!.academicLevel == AcademicLevel.BA3)

    // Navigate to the lesson creation screen
    composeTestRule.onNodeWithTag("middlePlus").performClick()

    // Create a new lesson
    composeTestRule.onNodeWithTag("titleField").performTextInput("Help how do I write tests")
    composeTestRule.onNodeWithTag("DescriptionField").performTextInput("CI is hard, pls help")
    composeTestRule.onNodeWithTag("DateButton").performClick()
    composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText("OK").isDisplayed() }
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithTag("TimeButton").performClick()
    composeTestRule.waitUntil(15000) { composeTestRule.onNodeWithText("OK").isDisplayed() }
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithTag("checkbox_ENGLISH").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("subjectButton").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("dropdownAICC").performClick()
    composeTestRule.onNodeWithTag("mapButton").performClick()
    composeTestRule.onNodeWithTag("mapContainer").performClick()

    composeTestRule.waitUntil(15000) {
      // wait max 4 seconds for the map to load,
      // as soon as the map is ready, the next line will be executed
      testMapReady
    }
    composeTestRule.waitUntil(20000) { composeTestRule.onNodeWithTag("googleMap").isDisplayed() }

    composeTestRule.onNodeWithTag("googleMap").performTouchInput { click(center) }

    composeTestRule.waitUntil(20000) {
      assertEnabledToBoolean(composeTestRule.onNodeWithTag("confirmLocation"))
    }
    testMapReady = false

    composeTestRule.onNodeWithTag("confirmLocation").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Do not select any tutors
    composeTestRule.onNodeWithTag("noTutorButton").performClick()

    // Assert lesson has correct fields
    assert(currentLesson!!.title == "Help how do I write tests")
    assert(currentLesson!!.description == "CI is hard, pls help")
    assert(currentLesson!!.subject == Subject.AICC)
    assert(currentLesson!!.languages == listOf(Language.ENGLISH))
    assert(currentLesson!!.status == LessonStatus.STUDENT_REQUESTED)

    // Check if the lesson is displayed
    composeTestRule.onNodeWithText("Help how do I write tests").assertIsDisplayed()
  }

  @Test
  fun endToEndStudentEditAccountAndLessonLifecycle() {

    // Simulate the user having a lesson
    currentLesson =
        Lesson(
            "mockUid",
            "Math Lesson",
            "This is a math lesson.",
            Subject.ANALYSIS,
            listOf(Language.ENGLISH),
            listOf(),
            "mockUid",
            5.0,
            30.0,
            0.0,
            "30/10/2024T10:00:00",
            LessonStatus.STUDENT_REQUESTED,
            0.0,
            0.0)

    composeTestRule.waitForIdle()

    // Sign in
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // Enter valid data for all fields
    composeTestRule.onNodeWithTag("firstNameField").performTextInput("Alice")
    composeTestRule.onNodeWithTag("lastNameField").performTextInput("Dupont")
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("1234567890")
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo()
    composeTestRule.onNodeWithTag("roleButtonStudent").performClick()

    // Select section and academic level
    composeTestRule.onNodeWithTag("sectionDropdown").performClick()
    composeTestRule.onNodeWithTag("sectionDropdownItem-SC").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdown").performClick()
    composeTestRule.onNodeWithTag("academicLevelDropdownItem-BA3").performClick()

    // Click the confirm button
    composeTestRule.onNodeWithTag("confirmButton").performScrollTo().performClick()

    composeTestRule.waitForIdle()

    composeTestRule.waitUntil(15000) {
      composeTestRule.onNodeWithTag("profile_photo", true).isDisplayed()
    }

    // Go to the profile viewing screen
    composeTestRule.onNodeWithTag("profile_photo", true).performClick()

    // Check if the correct profile info is displayed
    composeTestRule.onNodeWithText("Alice Dupont").assertIsDisplayed()
    composeTestRule.onNodeWithText("SC - BA3").assertIsDisplayed()

    // Go to the edit profile screen
    composeTestRule.onNodeWithTag("editProfileButton").performClick()

    // Change the profile info
    composeTestRule.onNodeWithTag("phoneNumberField").performTextInput("0")
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    verify(mockProfileRepository).updateProfile(any(), any(), any())

    // Navigate to the lesson creation screen
    composeTestRule.onNodeWithTag("closeButton").performClick()

    // Check if the lesson is displayed
    composeTestRule.onNodeWithText("Math Lesson").assertIsDisplayed()
    composeTestRule.onNodeWithText("Math Lesson").performClick()

    // Edit the lesson
    composeTestRule.onNodeWithTag("titleField").performClick()
    composeTestRule.onNodeWithTag("titleField").performTextClearance()
    composeTestRule.onNodeWithTag("titleField").performTextInput("NVM got it :)")
    composeTestRule.onNodeWithTag("confirmButton").performClick()

    // Check if the lesson was updated correctly
    composeTestRule.onNodeWithText("NVM got it :)").assertIsDisplayed()

    // Delete the lesson
    composeTestRule.onNodeWithText("NVM got it :)").performClick()
    composeTestRule.onNodeWithTag("deleteButton").performClick()
    verify(mockLessonRepository).deleteLesson(any(), any(), any())
    assert(currentLesson == null)

    // Check if the lesson was deleted correctly
    composeTestRule.onNodeWithTag("noLessonsText").assertIsDisplayed()

    // Simulate an open lesson being taken up by a tutor
    currentLesson =
        Lesson(
            "mockUid",
            "Help how do I write tests",
            "Teach me how to write tests pls",
            Subject.AICC,
            listOf(Language.ENGLISH),
            listOf("mockTutor"),
            "",
            0.0,
            0.0,
            30.0,
            "30/10/2024T10:00:00",
            LessonStatus.STUDENT_REQUESTED,
            0.0,
            0.0)

    // Reload the home screen and accept the lesson
    composeTestRule.onNodeWithTag("profile_photo", true).performClick()
    composeTestRule.onNodeWithTag("closeButton").performClick()
    composeTestRule.onNodeWithText("Help how do I write tests").performClick()
    composeTestRule.onNodeWithTag("tutorCard_0").performClick()
    composeTestRule.onNodeWithTag("confirmButton").performClick()
    composeTestRule.onNodeWithTag("confirmDialogButton").performClick()

    // Check if the lesson is updated and displayed
    composeTestRule.onNodeWithText("Help how do I write tests").assertIsDisplayed()
    assert(currentLesson!!.status == LessonStatus.CONFIRMED)

    // Simulate the lesson being completed
    currentLesson =
        Lesson(
            "mockUid",
            "Help how do I write tests",
            "Teach me how to write tests pls",
            Subject.AICC,
            listOf(Language.ENGLISH),
            listOf("mockTutor"),
            "mockUid",
            0.0,
            0.0,
            5.0,
            "30/10/2024T10:00:00",
            LessonStatus.COMPLETED,
            0.0,
            0.0)

    // Go to the profile info screen and check it is displayed
    composeTestRule.onNodeWithTag("profile_photo", true).performClick()
    composeTestRule.onNodeWithText("Help how do I write tests").assertIsDisplayed()
    composeTestRule.onNodeWithText("Help how do I write tests").performClick()
  }
}
