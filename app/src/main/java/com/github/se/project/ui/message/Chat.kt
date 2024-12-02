package com.github.se.project.ui.message

import android.annotation.SuppressLint
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

/**
 * A composable function that represents the chat screen for viewing and sending messages in a
 * specific channel.
 *
 * @param navigationActions Handles navigation actions to other screens.
 * @param chatViewModel ViewModel to manage chat-related data and actions.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatScreen(
    navigationActions: NavigationActions,
    chatViewModel: ChatViewModel,
) {
  // Observes the current channel ID from the chat ViewModel
  val channelID = chatViewModel.currentChannelID.collectAsState()

  // Redirects to the ChannelScreen if no channel is currently selected
  if (channelID.value == null) {
    navigationActions.navigateTo(Screen.CHANNEL)
  }

  // Retrieves the current context, used for displaying messages or initializing components
  val context = LocalContext.current

  // Sets the theme for chat-related UI components
  ChatTheme {
    // Displays the MessagesScreen for the selected channel
    MessagesScreen(
        // Factory to create a MessagesViewModel with the current channel and message limit
        viewModelFactory =
            MessagesViewModelFactory(
                context = context, channelId = channelID.value!!, messageLimit = 3000),
        // Callback triggered when the channel avatar is clicked
        onChannelAvatarClick = {
          Toast.makeText(context, "This is not supported at the moment.", Toast.LENGTH_LONG).show()
        },
        // Callback triggered when the back button is pressed
        onBackPressed = {
          // Clears the current channel ID and navigates back to the previous screen
          chatViewModel.setChannelID(null)
          navigationActions.goBack()
        })
  }
}
