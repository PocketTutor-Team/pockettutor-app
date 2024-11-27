package com.github.se.project.ui.message

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState

@Composable
fun ChannelScreen(
    navigationActions: NavigationActions,
    chatViewModel: ChatViewModel,
) {
  val clientInitialisationState = chatViewModel.clientInitialisationState.collectAsState()

  ChatTheme {
    when (clientInitialisationState.value) {
      InitializationState.COMPLETE -> {
        ChannelsScreen(
            title = "hello",
            onChannelClick = { channel ->
              chatViewModel.setChannelID(channel.id)
              navigationActions.navigateTo(Screen.CHAT)
            },
            onBackPressed = { navigationActions.goBack() })
      }
      InitializationState.INITIALIZING -> {
        Text(text = "Initialising...")
      }
      InitializationState.NOT_INITIALIZED -> {
        Text(text = "Not initialized...")
      }
    }
  }
}
