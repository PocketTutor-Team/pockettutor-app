package com.github.se.project.ui.message

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.BottomNavigationMenu
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_STUDENT
import com.github.se.project.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS_TUTOR
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.navigation.TopLevelDestinations
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState

/**
 * A composable function that represents the screen displaying a list of chat channels.
 *
 * @param navigationActions Handles navigation between different screens.
 * @param listProfilesViewModel ViewModel to fetch and observe user profiles.
 * @param chatViewModel ViewModel to manage chat-related data and actions.
 * @param lessonViewModel ViewModel to manage lesson-related data and actions.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChannelScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    chatViewModel: ChatViewModel,
    lessonViewModel: LessonViewModel // Use lessonViewModel to get confirmed lessons
) {
  // Observes the state of chat client initialization
  val clientInitialisationState = chatViewModel.clientInitialisationState.collectAsState()

  // Observes the current user's profile
  val currentProfile = listProfilesViewModel.currentProfile.collectAsState()

  // Fetches the list of confirmed lessons for the current user
  val confirmedLessons = lessonViewModel.currentUserLessons.collectAsState().value

  // Retrieves the current context, used for displaying messages or initializing components
  val context = LocalContext.current

  // Creates a list of chat group member identifiers based on confirmed lessons
  val chatGroupsMembers =
      confirmedLessons
          .filter { it.status == LessonStatus.CONFIRMED }
          .map { lesson -> lesson.tutorUid + lesson.studentUid }

  // Ensures chat channels exist for each group of chat members
  LaunchedEffect(chatGroupsMembers) {
    chatGroupsMembers.forEach { chatViewModel.ensureChannelExists(it) }
  }

  // Determines navigation items based on the current user's role
  val navigationItems =
      when (currentProfile.value?.role) {
        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
        Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
      }

  // Sets the theme for chat-related UI components
  ChatTheme {
    // Displays the UI based on the client initialization state
    when (clientInitialisationState.value) {
      InitializationState.COMPLETE -> {
        Scaffold(
            bottomBar = {
              // Displays the bottom navigation menu
              BottomNavigationMenu(
                  onTabSelect = { route ->
                    if (route == TopLevelDestinations.STUDENT) {
                      lessonViewModel.unselectLesson()
                    }
                    navigationActions.navigateTo(route)
                  },
                  tabList = navigationItems,
                  selectedItem = navigationActions.currentRoute())
            }) {
              // Displays the list of chat channels
              ChannelsScreen(
                  title = "Chat",
                  onHeaderActionClick = {
                    Toast.makeText(
                            context, "This is not supported at the moment.", Toast.LENGTH_LONG)
                        .show()
                  },
                  onHeaderAvatarClick = {
                    Toast.makeText(
                            context, "This is not supported at the moment.", Toast.LENGTH_LONG)
                        .show()
                  },
                  onChannelClick = { channel ->
                    // Logs the channel details and navigates to the chat screen
                    Log.e("HELLO", "channel: id: " + channel.id + " cid: " + channel.cid)
                    chatViewModel.setChannelID(channel.cid)
                    navigationActions.navigateTo(Screen.CHAT)
                  },
                  isShowingHeader = true,
              )
            }
      }
      InitializationState.INITIALIZING -> {
        // Displays a loading message during initialization
        Text(text = "Initializing...")
      }
      InitializationState.NOT_INITIALIZED -> {
        // Displays an error message if initialization fails
        Text(text = "Not initialized...")
      }
    }
  }
}
