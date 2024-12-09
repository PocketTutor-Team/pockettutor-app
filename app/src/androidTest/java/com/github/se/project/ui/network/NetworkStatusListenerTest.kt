package com.github.se.project.ui.network

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.* // Import testing APIs
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.ui.components.NetworkStatusListener
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class NetworkStatusListenerTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock NetworkStatusViewModel to control the network status state
  private val mockIsConnected = MutableStateFlow(true)

  private val mockNetworkStatusViewModel =
      object :
          NetworkStatusViewModel(
              application = androidx.test.core.app.ApplicationProvider.getApplicationContext()) {
        override val isConnected = mockIsConnected
      }

  @Test
  fun networkStatusListener_showsOfflineBanner_whenOffline() {
    // Set initial state to offline
    mockIsConnected.value = false

    composeTestRule.setContent {
      NetworkStatusListener(networkStatusViewModel = mockNetworkStatusViewModel) {
        // Provide dummy content
        Text("Online Content", modifier = Modifier.testTag("online_content"))
      }
    }

    // Check if the offline banner is displayed
    composeTestRule
        .onNodeWithTag("offline_text")
        .assertExists()
        .assertTextEquals("No internet connection")

    composeTestRule.onNodeWithTag("offline_icon").assertExists()

    // Check that the content is still displayed
    composeTestRule.onNodeWithTag("online_content").assertExists()
  }

  @Test
  fun networkStatusListener_hidesOfflineBanner_whenOnline() {
    // Set initial state to online
    mockIsConnected.value = true

    composeTestRule.setContent {
      NetworkStatusListener(networkStatusViewModel = mockNetworkStatusViewModel) {
        // Provide dummy content
        Text("Online Content", modifier = Modifier.testTag("online_content"))
      }
    }

    // Check if the offline banner is not displayed
    composeTestRule.onNodeWithTag("offline_text").assertDoesNotExist()
    composeTestRule.onNodeWithTag("offline_icon").assertDoesNotExist()

    // Check that the content is still displayed
    composeTestRule.onNodeWithTag("online_content").assertExists()
  }

  @Test
  fun networkStatusListener_displaysAndHidesBanner_whenNetworkStatusChanges() {
    // Start with offline state
    mockIsConnected.value = false

    composeTestRule.setContent {
      NetworkStatusListener(networkStatusViewModel = mockNetworkStatusViewModel) {
        // Provide dummy content
        Text("Online Content", modifier = Modifier.testTag("online_content"))
      }
    }

    // Initially offline: check the offline banner is shown
    composeTestRule
        .onNodeWithTag("offline_text")
        .assertExists()
        .assertTextEquals("No internet connection")

    // Change to online
    mockIsConnected.value = true
    composeTestRule.waitForIdle() // Ensure the state is updated

    // Now online: check the offline banner is hidden
    composeTestRule.onNodeWithTag("offline_text").assertDoesNotExist()

    // Change back to offline
    mockIsConnected.value = false
    composeTestRule.waitForIdle() // Ensure the state is updated

    // Check that the offline banner is shown again
    composeTestRule
        .onNodeWithTag("offline_text")
        .assertExists()
        .assertTextEquals("No internet connection")
  }
}
