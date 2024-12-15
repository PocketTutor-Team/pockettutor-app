package com.github.se.project.ui.message

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.models.InitializationState
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChannelScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  lateinit var mockChatViewModel: ChatViewModel

  lateinit var mockLessonViewModel: LessonViewModel

  lateinit var mockListProfilesViewModel: ListProfilesViewModel

  lateinit var mockNavigationActions: NavigationActions

  private val profileFlow = MutableStateFlow<Profile?>(null)
  private val lessonsFlow = MutableStateFlow(emptyList<Lesson>())
  private val clientStateFlow = MutableStateFlow(InitializationState.NOT_INITIALIZED)
  private val mockNetworkStatusViewModel = mockk<NetworkStatusViewModel>(relaxed = true)

  private val mockCurrentProfile = MutableStateFlow<Profile?>(null)
  private val mockInitializationState = MutableStateFlow(InitializationState.COMPLETE)
  private val mockConfirmedLessons = MutableStateFlow(emptyList<Lesson>())

  @Before
  fun setup() {
    MockKAnnotations.init(this)
    every { mockListProfilesViewModel.currentProfile } returns mockCurrentProfile
    every { mockChatViewModel.clientInitializationState } returns mockInitializationState
    every { mockLessonViewModel.currentUserLessons } returns mockConfirmedLessons
  }

  @Test
  fun testConnectsUserWhenProfileIsAvailable() {
    val testProfile =
        Profile(
            uid = "student123",
            token = "",
            googleUid = "mockTutor",
            firstName = "Ozymandias",
            lastName = "Halifax",
            phoneNumber = "1234567890",
            role = Role.STUDENT,
            section = Section.IN,
            academicLevel = AcademicLevel.BA3,
            languages = listOf(Language.ENGLISH),
            subjects = listOf(Subject.AICC),
            schedule = List(7) { List(12) { 0 } })
    profileFlow.value = testProfile

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    verify { mockChatViewModel.connectUser(testProfile) }
  }

  @Test
  fun testShowsChannelsScreenWhenInitialized() {
    clientStateFlow.value = InitializationState.COMPLETE

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    composeTestRule.onNodeWithText("PocketTutor Chat").assertExists()
  }

  @Test
  fun testNavigatesToChatWhenChannelClicked() {
    clientStateFlow.value = InitializationState.COMPLETE

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    composeTestRule.onNodeWithText("PocketTutor Chat").assertExists()
    // Simulate channel click (mock channel handling logic needed)
    every { mockChatViewModel.setCurrentChannelId(any()) } just Runs
  }

  @Test
  fun testNavigationOnChannelClick() {
    // Arrange
    val testChannelId = "test_channel_id"
    every { mockChatViewModel.setCurrentChannelId(testChannelId) } answers {}

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    // Act: Simulate clicking a channel
    composeTestRule.onNodeWithText("PocketTutor Chat").performClick()

    // Assert: Verify navigation to the Chat screen and the channel ID was set
    verify { mockChatViewModel.setCurrentChannelId(testChannelId) }
    verify { mockNavigationActions.navigateTo(Screen.CHAT) }
  }

  @Test
  fun testUIWhenInitializationComplete() {
    // Arrange
    mockInitializationState.value = InitializationState.COMPLETE

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    // Assert: Check if the ChannelsScreen is displayed
    composeTestRule.onNodeWithText("PocketTutor Chat").assertIsDisplayed()
  }

  @Test
  fun testUIWhenInitializing() {
    // Arrange
    mockInitializationState.value = InitializationState.INITIALIZING

    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    // Assert: Verify that the initializing state UI is displayed
    composeTestRule.onNodeWithText("Initializing...").assertExists() // Placeholder
  }

  @Test
  fun testBottomNavigationMenu() {
    // Arrange
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = mockNavigationActions,
          listProfilesViewModel = mockListProfilesViewModel,
          chatViewModel = mockChatViewModel,
          lessonViewModel = mockLessonViewModel,
          networkStatusViewModel = mockNetworkStatusViewModel)
    }

    // Act: Click the Home tab in the bottom navigation
    composeTestRule.onNodeWithText("Home").performClick()

    // Assert: Verify navigation to the Home screen
    verify { mockNavigationActions.navigateTo(Screen.HOME) }
  }
}
