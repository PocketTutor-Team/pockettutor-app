package com.github.se.project.ui.message

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChannelScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var listProfilesViewModel: ListProfilesViewModel
  private lateinit var lessonViewModel: LessonViewModel

  @Before
  fun setup() {
    // Mocking the dependencies
    navigationActions = mockk(relaxed = true)
    chatViewModel = mockk(relaxed = true)
    listProfilesViewModel = mockk(relaxed = true)
    lessonViewModel = mockk(relaxed = true)

    // Mocking necessary flows for state
    val currentProfileFlow: MutableStateFlow<Profile?> = MutableStateFlow(null)
    every { listProfilesViewModel.currentProfile } returns currentProfileFlow

    // Mocking confirmed lessons
    val confirmedLessonsFlow: MutableStateFlow<List<Lesson>> = MutableStateFlow(emptyList())
    every { lessonViewModel.currentUserLessons } returns confirmedLessonsFlow
  }

  @Test
  fun testChannelInitializationComplete() {
    // Setting up mock behavior for initialization state
    val initializationStateFlow: MutableStateFlow<InitializationState> =
        MutableStateFlow(InitializationState.COMPLETE)
    every { chatViewModel.clientInitialisationState } returns initializationStateFlow

    // Mocking a Channel instance
    val mockChannel =
        mockk<Channel> {
          every { cid } returns "mockChannelId"
          every { id } returns "mockId"
        }

    // Setting up the composable content
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = navigationActions,
          listProfilesViewModel = listProfilesViewModel,
          chatViewModel = chatViewModel,
          lessonViewModel = lessonViewModel)
    }

    // Test: Verify that ChannelsScreen is displayed
    composeTestRule.onNodeWithText("Chat").assertExists()

    // Simulate clicking a channel
    composeTestRule.onNodeWithTag("Channel-Item").performClick()

    // Verify that the correct navigation action is called to go to ChatScreen
    verify { chatViewModel.setChannelID("mockChannelId") }
    verify { navigationActions.navigateTo(Screen.CHAT) }
  }

  @Test
  fun testChannelInitializationLoading() {
    // Setting up mock behavior for initialization state
    val initializationStateFlow: MutableStateFlow<InitializationState> =
        MutableStateFlow(InitializationState.INITIALIZING)
    every { chatViewModel.clientInitialisationState } returns initializationStateFlow

    // Setting up the composable content
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = navigationActions,
          listProfilesViewModel = listProfilesViewModel,
          chatViewModel = chatViewModel,
          lessonViewModel = lessonViewModel)
    }

    // Test: Verify that the loading message is displayed
    composeTestRule.onNodeWithText("Initializing...").assertExists()
  }

  @Test
  fun testChannelInitializationError() {
    // Setting up mock behavior for initialization state
    val initializationStateFlow: MutableStateFlow<InitializationState> =
        MutableStateFlow(InitializationState.NOT_INITIALIZED)
    every { chatViewModel.clientInitialisationState } returns initializationStateFlow

    // Setting up the composable content
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = navigationActions,
          listProfilesViewModel = listProfilesViewModel,
          chatViewModel = chatViewModel,
          lessonViewModel = lessonViewModel)
    }

    // Test: Verify that the error message is displayed
    composeTestRule.onNodeWithText("Not initialized...").assertExists()
  }

  @Test
  fun testNavigationToHome() {
    // Setting up mock behavior for initialization state
    val initializationStateFlow: MutableStateFlow<InitializationState> =
        MutableStateFlow(InitializationState.COMPLETE)
    every { chatViewModel.clientInitialisationState } returns initializationStateFlow

    // Setting up the composable content
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = navigationActions,
          listProfilesViewModel = listProfilesViewModel,
          chatViewModel = chatViewModel,
          lessonViewModel = lessonViewModel)
    }

    // Simulate back navigation (onBackPressed)
    composeTestRule.onNodeWithTag("BackButton").performClick()

    // Verify that the navigation action to go to HOME screen was called
    verify { navigationActions.navigateTo(Screen.HOME) }
  }

  @Test
  fun testRoleBasedNavigation() {
    // Setting the role to STUDENT
    val currentProfileFlow: MutableStateFlow<Profile?> =
        MutableStateFlow(
            Profile(
                uid = "mockUid",
                googleUid = "mockGoogleUid",
                firstName = "John",
                lastName = "Doe",
                phoneNumber = "1234567890",
                role = Role.STUDENT,
                section = Section.AR,
                academicLevel = AcademicLevel.BA1))

    every { listProfilesViewModel.currentProfile } returns currentProfileFlow

    // Setting up the composable content
    composeTestRule.setContent {
      ChannelScreen(
          navigationActions = navigationActions,
          listProfilesViewModel = listProfilesViewModel,
          chatViewModel = chatViewModel,
          lessonViewModel = lessonViewModel)
    }

    // Verify navigation items for student role
    composeTestRule.onNodeWithText("Student Navigation Item").assertExists()
  }
}
