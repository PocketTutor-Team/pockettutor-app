// ChatScreen.kt
package com.github.se.project.ui.message

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory

@Composable
fun ChatScreen(navigationActions: NavigationActions, chatViewModel: ChatViewModel) {
  val context = LocalContext.current
  val channelId by chatViewModel.currentChannelId.collectAsState()

  if (channelId == null) {
    navigationActions.navigateTo(Screen.CHANNEL)
    return
  }

  ChatTheme {
    MessagesScreen(
        viewModelFactory = MessagesViewModelFactory(context = context, channelId = channelId!!),
        onChannelAvatarClick = {
          Toast.makeText(context, "This is not supported at the moment.", Toast.LENGTH_LONG).show()
        },
        onBackPressed = { navigationActions.navigateTo(Screen.CHANNEL) })
  }
}
