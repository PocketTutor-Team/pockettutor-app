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
import com.github.se.project.model.lesson.Lesson
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChannelScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    chatViewModel: ChatViewModel,
    lessonViewModel: LessonViewModel, // Use lessonViewModel to get confirmed lessons
) {
  val clientInitialisationState = chatViewModel.clientInitialisationState.collectAsState()
  val currentProfile = listProfilesViewModel.currentProfile.collectAsState()
    val confirmedLessons = lessonViewModel.currentUserLessons.collectAsState().value

    // chatGroupsMembers contains the uid of the tutors and student for each confirmed lesson
    val chatGroupsMembers =
      confirmedLessons
          .filter { it.status == LessonStatus.CONFIRMED }
          .map { lesson ->
            lesson.tutorUid + lesson.studentUid
          }

    val context = LocalContext.current

    LaunchedEffect(chatGroupsMembers) {
        chatGroupsMembers.forEach {
            chatViewModel.ensureChannelExists(it)
        }
    }

    val navigationItems =
        when (currentProfile.value?.role) {
            Role.TUTOR -> LIST_TOP_LEVEL_DESTINATIONS_TUTOR
            Role.STUDENT -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
            else -> LIST_TOP_LEVEL_DESTINATIONS_STUDENT
        }

  ChatTheme {
    when (clientInitialisationState.value) {
      InitializationState.COMPLETE -> {
          Scaffold(
            bottomBar = {
                BottomNavigationMenu(
                    onTabSelect = { route ->
                        if (route == TopLevelDestinations.STUDENT) {
                            lessonViewModel.unselectLesson()
                        }
                        navigationActions.navigateTo(route)
                    },
                    tabList = navigationItems,
                    selectedItem = navigationActions.currentRoute()
                )
            }
          ) {
              ChannelsScreen(
                  title = "Chat",
                  onChannelClick = { channel ->
                      Log.e("HELLO", "channel: id: " + channel.id + " cid: " + channel.cid)
                      chatViewModel.setChannelID(channel.cid)
                      navigationActions.navigateTo(Screen.CHAT)
                  },
                  isShowingHeader = false,
                  onBackPressed = { navigationActions.navigateTo(Screen.HOME) })
          }
      }
      InitializationState.INITIALIZING -> {
        Text(text = "Initializing...")
      }
      InitializationState.NOT_INITIALIZED -> {
        Text(text = "Not initialized...")
      }
    }
  }
}
