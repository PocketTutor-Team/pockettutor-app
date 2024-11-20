package com.github.se.project.ui.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory

@Composable
fun ChatScreen(
    navigationActions: NavigationActions,
    chatViewModel: ChatViewModel,
) {
  val channelID = chatViewModel.currentChannelID.collectAsState()

  if (channelID.value == null) {
    navigationActions.navigateTo(Screen.CHANNEL)
  }

  val context = LocalContext.current

  ChatTheme {
    MessagesScreen(
        viewModelFactory =
            MessagesViewModelFactory(
                context = context, channelId = channelID.value!!, messageLimit = 3000),
        onBackPressed = {
          chatViewModel.setChannelID(null)
          navigationActions.goBack()
        })
  }
}
