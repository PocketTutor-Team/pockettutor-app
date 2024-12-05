package com.github.se.project

import CompletedLessonScreen
import SplashScreen
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.github.se.project.model.authentification.AuthenticationViewModel
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.EpflVerificationRepository
import com.github.se.project.model.chat.ChatViewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.authentification.SignInScreen
import com.github.se.project.ui.components.NetworkStatusListener
import com.github.se.project.ui.lesson.AddLessonScreen
import com.github.se.project.ui.lesson.ConfirmedLessonScreen
import com.github.se.project.ui.lesson.EditRequestedLessonScreen
import com.github.se.project.ui.lesson.RequestedLessonsScreen
import com.github.se.project.ui.lesson.SelectedTutorDetailsScreen
import com.github.se.project.ui.lesson.TutorLessonResponseScreen
import com.github.se.project.ui.lesson.TutorMatchingScreen
import com.github.se.project.ui.message.ChannelScreen
import com.github.se.project.ui.message.ChatScreen
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.overview.HomeScreen
import com.github.se.project.ui.profile.CreateProfileScreen
import com.github.se.project.ui.profile.CreateTutorProfile
import com.github.se.project.ui.profile.CreateTutorSchedule
import com.github.se.project.ui.profile.EditProfile
import com.github.se.project.ui.profile.EditTutorSchedule
import com.github.se.project.ui.profile.ProfileInfoScreen
import com.github.se.project.ui.theme.SampleAppTheme
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {

    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        NetworkStatusListener {
          Surface(modifier = Modifier.fillMaxSize()) {
            PocketTutorApp(
                authenticationViewModel = viewModel(),
                listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory),
                lessonViewModel = viewModel(factory = LessonViewModel.Factory),
                networkStatusViewModel = viewModel(),
                certificationViewModel =
                    viewModel(
                        factory =
                            CertificationViewModel.Factory(
                                EpflVerificationRepository(),
                                viewModel(factory = ListProfilesViewModel.Factory))),
                onMapReadyChange = {},
                chatViewModel = viewModel(factory = ChatViewModel.Factory(buildChatClient())))
          }
        }
      }
    }
  }

  private fun buildChatClient(): ChatClient {
    val offlinePluginFactory =
        StreamOfflinePluginFactory(
            appContext = applicationContext,
        )
    val statePluginFactory =
        StreamStatePluginFactory(
            config =
                StatePluginConfig(
                    backgroundSyncEnabled = true,
                    userPresence = true,
                ),
            appContext = this,
        )

    val client =
        ChatClient.Builder(getString(R.string.chat_api_key), applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

    return client
  }
}

@Composable
fun PocketTutorApp(
    testMode: Boolean = false,
    authenticationViewModel: AuthenticationViewModel,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    networkStatusViewModel: NetworkStatusViewModel,
    certificationViewModel: CertificationViewModel,
    onMapReadyChange: (Boolean) -> Unit = {},
    chatViewModel: ChatViewModel,
) {
  // Navigation
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val context = LocalContext.current

  // put it in a box to be able to display the loading indicator
  Box(modifier = Modifier.fillMaxSize()) {
    // Navigation host for the app
    NavHost(navController = navController, startDestination = Screen.SPLASH) {
      composable(Screen.SPLASH) { SplashScreen(navController, listProfilesViewModel) }
      composable(Screen.AUTH) {
        if (testMode) {
          SignInScreen(onSignInClick = { navigationActions.navigateTo(Screen.CREATE_PROFILE) })
        } else {
          SignInScreen(
              onSignInClick = {
                authenticationViewModel.handleGoogleSignIn(
                    context,
                    onSuccess = { _ ->
                      // Navigate back to SplashScreen to let it handle navigation
                      navController.navigate(Screen.SPLASH) {
                        popUpTo(Screen.AUTH) { inclusive = true }
                      }
                    })
              })
        }
      }

      composable(Screen.HOME) {
        HomeScreen(
            listProfilesViewModel,
            lessonViewModel,
            networkStatusViewModel,
            chatViewModel,
            navigationActions)
      }
      composable(Screen.CREATE_PROFILE) {
        CreateProfileScreen(
            navigationActions, listProfilesViewModel, certificationViewModel, testMode)
      }
      composable(Screen.CREATE_TUTOR_PROFILE) {
        CreateTutorProfile(navigationActions, listProfilesViewModel)
      }
      composable(Screen.CREATE_TUTOR_SCHEDULE) {
        CreateTutorSchedule(navigationActions, listProfilesViewModel)
      }
      composable(Screen.PROFILE) {
        ProfileInfoScreen(
            navigationActions, listProfilesViewModel, lessonViewModel, authenticationViewModel)
      }
      composable(Screen.ADD_LESSON) {
        AddLessonScreen(
            navigationActions,
            listProfilesViewModel,
            lessonViewModel,
            networkStatusViewModel,
            onMapReadyChange = onMapReadyChange)
      }
      composable(Screen.CHANNEL) {
        ChannelScreen(
            navigationActions,
            listProfilesViewModel,
            chatViewModel,
            lessonViewModel,
            networkStatusViewModel)
      }
      composable(Screen.CHAT) { ChatScreen(navigationActions, chatViewModel) }

      navigation(
          startDestination = Screen.LESSONS_REQUESTED,
          route = Route.FIND_STUDENT,
      ) {
        composable(Screen.HOME) {
          HomeScreen(
              listProfilesViewModel,
              lessonViewModel,
              networkStatusViewModel,
              chatViewModel,
              navigationActions)
        }
        composable(Screen.LESSONS_REQUESTED) {
          RequestedLessonsScreen(
              listProfilesViewModel, lessonViewModel, networkStatusViewModel, navigationActions)
        }
        composable(Screen.TUTOR_LESSON_RESPONSE) {
          TutorLessonResponseScreen(
              listProfilesViewModel, lessonViewModel, networkStatusViewModel, navigationActions)
        }
        composable(Screen.CONFIRMED_LESSON) {
          ConfirmedLessonScreen(
              listProfilesViewModel, lessonViewModel, networkStatusViewModel, navigationActions)
        }
        composable(Screen.COMPLETED_LESSON) {
          CompletedLessonScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
      }

      navigation(
          startDestination = Screen.HOME,
          route = Route.HOME,
      ) {
        composable(Screen.HOME) {
          HomeScreen(
              listProfilesViewModel,
              lessonViewModel,
              networkStatusViewModel,
              chatViewModel,
              navigationActions)
        }
        composable(Screen.EDIT_PROFILE) { EditProfile(navigationActions, listProfilesViewModel) }
        composable(Screen.EDIT_SCHEDULE) {
          EditTutorSchedule(navigationActions, listProfilesViewModel)
        }
        composable(Screen.EDIT_REQUESTED_LESSON) {
          EditRequestedLessonScreen(
              navigationActions,
              listProfilesViewModel,
              lessonViewModel,
              networkStatusViewModel,
              onMapReadyChange = onMapReadyChange)
        }
        composable(Screen.TUTOR_LESSON_RESPONSE) {
          TutorLessonResponseScreen(
              listProfilesViewModel, lessonViewModel, networkStatusViewModel, navigationActions)
        }
      }

      navigation(
          startDestination = Screen.ADD_LESSON,
          route = Route.FIND_TUTOR,
      ) {
        composable(Screen.HOME) {
          HomeScreen(
              listProfilesViewModel,
              lessonViewModel,
              networkStatusViewModel,
              chatViewModel,
              navigationActions)
        }
        composable(Screen.ADD_LESSON) {
          AddLessonScreen(
              navigationActions,
              listProfilesViewModel,
              lessonViewModel,
              networkStatusViewModel,
              onMapReadyChange = onMapReadyChange)
        }
        composable(Screen.TUTOR_MATCH) {
          TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
        composable(Screen.SELECTED_TUTOR_DETAILS) {
          SelectedTutorDetailsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
        composable(Screen.COMPLETED_LESSON) {
          CompletedLessonScreen(listProfilesViewModel, lessonViewModel, navigationActions)
        }
      }
    }
  }
}
