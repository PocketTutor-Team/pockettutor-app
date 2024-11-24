package com.github.se.project.ui.message

import android.util.Log
import android.widget.Toast
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

    Log.e("HELLO", "channel: id: " + channelID.value)

  if (channelID.value == null) {
    navigationActions.navigateTo(Screen.CHANNEL)
      return
  }

  val context = LocalContext.current

  ChatTheme {
    MessagesScreen(
        viewModelFactory =
            MessagesViewModelFactory(
                context = context, channelId = channelID.value!!, messageLimit = 3000),
        onChannelAvatarClick = { Toast.makeText(context, "This is not supported at the moment.", Toast.LENGTH_LONG).show()},
        onBackPressed = {
          chatViewModel.setChannelID(null)
          navigationActions.goBack()
        })
  }
}
