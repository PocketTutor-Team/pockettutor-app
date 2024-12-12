package com.github.se.project.ui.message

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.navigation.*
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChannelScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    chatViewModel: ChatViewModel,
    lessonViewModel: LessonViewModel
) {
  val currentProfile by listProfilesViewModel.currentProfile.collectAsState()
  val clientInitializationState by chatViewModel.clientInitializationState.collectAsState()

  // Retrieves the current context, used for displaying messages or initializing components
  val context = LocalContext.current

  // Connect the user when the profile is available
  LaunchedEffect(currentProfile) {
    currentProfile?.let { profile -> chatViewModel.connectUser(profile) }
  }
  // Observe confirmed lessons and create channels
  val confirmedLessons by lessonViewModel.currentUserLessons.collectAsState()
  LaunchedEffect(confirmedLessons) {
    val profiles = listProfilesViewModel.profiles.value

    confirmedLessons
        .filter { it.status == LessonStatus.CONFIRMED }
        .forEach { lesson ->
          val lessonTitle = lesson.title

          chatViewModel.createOrGetChannel(
              lesson,
              { channel -> Log.d("LaunchedEffect", "Channel created or retrieved: ${channel.id}") },
              lessonTitle)
        }
  }
  // Determine navigation items based on the user's role
  val navigationItems =
      when (currentProfile?.role) {
        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
        Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
      }

  ChatTheme {
    when (clientInitializationState) {
      InitializationState.COMPLETE -> {
        Scaffold(
            bottomBar = {
              BottomNavigationMenu(
                  onTabSelect = { route -> navigationActions.navigateTo(route) },
                  tabList =
                      when (currentProfile?.role) {
                        Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
                        Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
                        else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
                      },
                  selectedItem = navigationActions.currentRoute())
            }) {
              ChannelsScreen(
                  title = "PocketTutor Chat",
                  isShowingHeader = true,
                  onChannelClick = { channel ->
                    // val otherUsername = channel.extraData["name"] as? String ?: "Unknown"
                    chatViewModel.setCurrentChannelId(channel.cid)
                    navigationActions.navigateTo(Screen.CHAT)
                  },
                  onBackPressed = { navigationActions.navigateTo(Screen.HOME) },
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
              )
            }
      }
      InitializationState.INITIALIZING -> {
        // Show initializing state
      }
      InitializationState.NOT_INITIALIZED -> {
        // Show not initialized state
      }
    }
  }
}
