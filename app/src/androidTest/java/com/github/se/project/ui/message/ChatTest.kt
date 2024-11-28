package com.github.se.project.ui.message

import android.widget.Toast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testNavigateToChannelWhenChannelIDIsNull() {
    // Mocking the dependencies
    val navigationActions = mockk<NavigationActions>(relaxed = true)
    val chatViewModel = mockk<ChatViewModel>(relaxed = true)

    // Simulating a null channel ID by returning null from collectAsState
    every { chatViewModel.currentChannelID } returns MutableStateFlow(null)

    // Setting up the composable content
    composeTestRule.setContent {
      ChatScreen(navigationActions = navigationActions, chatViewModel = chatViewModel)
    }

    // Verify that navigateTo is called for the CHANNEL screen
    verify { navigationActions.navigateTo(Screen.CHANNEL) }
  }

  @Test
  fun testBackButtonAction() {
    // Mocking the dependencies
    val navigationActions = mockk<NavigationActions>(relaxed = true)
    val chatViewModel = mockk<ChatViewModel>(relaxed = true)

    // Simulating a non-null channel ID by returning a mock value
    every { chatViewModel.currentChannelID } returns MutableStateFlow("mockChannelId")

    // Setting up the composable content
    composeTestRule.setContent {
      ChatScreen(navigationActions = navigationActions, chatViewModel = chatViewModel)
    }

    // Simulate a back button press
    composeTestRule.onNodeWithContentDescription("Back").performClick()

    // Verify that the channel ID is set to null and the navigation goes back
    verify { chatViewModel.setChannelID(null) }
    verify { navigationActions.goBack() }
  }

  @Test
  fun testToastOnAvatarClick() {
    // Mocking the dependencies
    val context = mockk<android.content.Context>(relaxed = true)
    val navigationActions = mockk<NavigationActions>(relaxed = true)
    val chatViewModel = mockk<ChatViewModel>(relaxed = true)

    // Simulating a non-null channel ID by returning a mock value
    every { chatViewModel.currentChannelID } returns MutableStateFlow("mockChannelId")

    // Stubbing Toast.makeText to return a mocked Toast object
    val toast = mockk<Toast>(relaxed = true)
    every {
      Toast.makeText(context, "This is not supported at the moment.", Toast.LENGTH_LONG)
    } returns toast

    // Setting up the composable content
    composeTestRule.setContent {
      ChatScreen(navigationActions = navigationActions, chatViewModel = chatViewModel)
    }

    // Simulate clicking on the avatar
    composeTestRule.onNodeWithTag("Avatar").performClick()

    // Verify that the Toast is shown
    verify { toast.show() }
  }

  @Test
  fun testMessagesScreenInitialization() {
    // Mocking the dependencies
    val context = mockk<android.content.Context>(relaxed = true)
    val navigationActions = mockk<NavigationActions>(relaxed = true)
    val chatViewModel = mockk<ChatViewModel>(relaxed = true)

    // Simulating a non-null channel ID by returning a mock value
    every { chatViewModel.currentChannelID } returns MutableStateFlow("mockChannelId")

    // You should mock the `MessagesViewModelFactory` instead of directly verifying
    val messagesViewModelFactory = mockk<MessagesViewModelFactory>(relaxed = true)

    // Setting up the composable content
    composeTestRule.setContent {
      ChatScreen(navigationActions = navigationActions, chatViewModel = chatViewModel)
    }

    // Verify that the MessagesViewModelFactory is initialized correctly
    verify {
      MessagesViewModelFactory(context = context, channelId = "mockChannelId", messageLimit = 3000)
    }
  }
}
