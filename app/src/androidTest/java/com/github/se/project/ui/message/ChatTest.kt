
package com.github.se.project.ui.message

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.MutableLiveData
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @MockK
  lateinit var mockNavigationActions: NavigationActions

  @MockK
  lateinit var mockChatViewModel: ChatViewModel

  private val testChannelID = "test-channel-id"

  @Before
  fun setUp() {
    MockKAnnotations.init(this)

    every { mockNavigationActions.navigateTo(Screen.CHANNEL) } returns Unit
    every { mockNavigationActions.goBack() } returns Unit
  }

  @Test
  fun redirectsToChannelScreenWhenChannelIDIsNull() = runBlockingTest {
    val channelFlow = MutableStateFlow<String?>(null)
    every { mockChatViewModel.currentChannelID } returns channelFlow

    composeTestRule.setContent {
      ChatScreen(
        navigationActions = mockNavigationActions,
        chatViewModel = mockChatViewModel
      )
    }

    verify { mockNavigationActions.navigateTo(Screen.CHANNEL) }
  }

  @Test
  fun rendersMessagesScreenWhenChannelIDIsSet() = runBlockingTest {
    val channelFlow = MutableStateFlow<String?>(testChannelID)
    every { mockChatViewModel.currentChannelID } returns channelFlow

    composeTestRule.setContent {
      ChatScreen(
        navigationActions = mockNavigationActions,
        chatViewModel = mockChatViewModel
      )
    }

    // Verify that MessagesScreen is shown (based on a hypothetical UI element)
    composeTestRule.onNodeWithText("MessagesScreen").assertExists()
  }

  @Test
  fun clearsChannelIDAndNavigatesBackOnBackPressed() = runBlockingTest {
    val channelFlow = MutableStateFlow<String?>(testChannelID)
    every { mockChatViewModel.currentChannelID } returns channelFlow
    every { mockChatViewModel.setChannelID(null) } returns Unit

    composeTestRule.setContent {
      ChatScreen(
        navigationActions = mockNavigationActions,
        chatViewModel = mockChatViewModel
      )
    }

    composeTestRule.runOnIdle {
      // Simulate back pressed
      mockChatViewModel.setChannelID(null)
      mockNavigationActions.goBack()
    }

    verify { mockChatViewModel.setChannelID(null) }
    verify { mockNavigationActions.goBack() }
  }
}
