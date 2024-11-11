package com.github.se.project

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.authentification.SignInScreen
import com.github.se.project.ui.lesson.AddLessonScreen
import com.github.se.project.ui.lesson.EditRequestedLessonScreen
import com.github.se.project.ui.lesson.RequestedLessonsScreen
import com.github.se.project.ui.lesson.TutorLessonResponseScreen
import com.github.se.project.ui.lesson.TutorMatchingScreen
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

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          PocketTutorApp(
              authenticationViewModel = viewModel(),
              listProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory),
              lessonViewModel = viewModel(factory = LessonViewModel.Factory))
        }
      }
    }
  }
}

@Composable
fun PocketTutorApp(
    testMode: Boolean = false,
    authenticationViewModel: AuthenticationViewModel,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel
) {
  // Navigation
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val profiles = listProfilesViewModel.profiles

  // Google user unique id (as var to be able to pass from the SignIn to CreateProfile screens)
  var googleUid = ""

  // Context
  val context = LocalContext.current

  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication flow
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      composable(Screen.AUTH) {
        if (testMode) {
          SignInScreen(
              onSignInClick = {
                googleUid = "testingUid"
                navigationActions.navigateTo(Screen.CREATE_PROFILE)
              })
        } else {
          SignInScreen(
              onSignInClick = {
                authenticationViewModel.handleGoogleSignIn(
                    context,
                    onSuccess = { uid ->
                      googleUid = uid
                      val profile = profiles.value.find { it.googleUid == googleUid }

                      if (profile != null) {
                        // If the user already has a profile, navigate to the home screen
                        listProfilesViewModel.setCurrentProfile(profile)
                        navigationActions.navigateTo(Screen.HOME)
                      } else {
                        // If the user doesn't have a profile, navigate to the profile creation
                        // screen
                        navigationActions.navigateTo(Screen.CREATE_PROFILE)
                      }
                    })
              })
        }
      }

      composable(Screen.HOME) {
        HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
      composable(Screen.CREATE_PROFILE) {
        CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid)
      }
      composable(Screen.CREATE_TUTOR_PROFILE) {
        CreateTutorProfile(navigationActions, listProfilesViewModel)
      }
      composable(Screen.CREATE_TUTOR_SCHEDULE) {
        CreateTutorSchedule(navigationActions, listProfilesViewModel)
      }
      composable(Screen.PROFILE) {
        ProfileInfoScreen(navigationActions, listProfilesViewModel, lessonViewModel)
      }
      composable(Screen.ADD_LESSON) {
        AddLessonScreen(navigationActions, listProfilesViewModel, lessonViewModel)
      }
    }

    navigation(
        startDestination = Screen.LESSONS_REQUESTED,
        route = Route.FIND_STUDENT,
    ) {
      composable(Screen.HOME) {
        HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
      composable(Screen.LESSONS_REQUESTED) {
        RequestedLessonsScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
      composable(Screen.TUTOR_LESSON_RESPONSE) {
        TutorLessonResponseScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.HOME,
        route = Route.HOME,
    ) {
      composable(Screen.HOME) {
        HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
      composable(Screen.EDIT_PROFILE) { EditProfile(navigationActions, listProfilesViewModel) }
      composable(Screen.EDIT_SCHEDULE) {
        EditTutorSchedule(navigationActions, listProfilesViewModel)
      }
      composable(Screen.EDIT_REQUESTED_LESSON) {
        EditRequestedLessonScreen(navigationActions, listProfilesViewModel, lessonViewModel)
      }
      composable(Screen.TUTOR_LESSON_RESPONSE) {
        TutorLessonResponseScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
    }

    navigation(
        startDestination = Screen.ADD_LESSON,
        route = Route.FIND_TUTOR,
    ) {
      composable(Screen.HOME) {
        HomeScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
      composable(Screen.ADD_LESSON) {
        AddLessonScreen(navigationActions, listProfilesViewModel, lessonViewModel)
      }
      composable(Screen.TUTOR_MATCH) {
        TutorMatchingScreen(listProfilesViewModel, lessonViewModel, navigationActions)
      }
    }
  }
}
