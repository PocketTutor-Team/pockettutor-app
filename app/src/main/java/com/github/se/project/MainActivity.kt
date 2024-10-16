package com.github.se.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.github.se.project.model.authentification.AuthenticationViewModel
import com.github.se.project.model.lesson.LessonsViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.resources.C
import com.github.se.project.ui.authentification.SignInScreen
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.profile.AvailabilityScreen
import com.github.se.project.ui.profile.CreateProfileScreen
import com.github.se.project.ui.profile.ProfileInfoScreen
import com.github.se.project.ui.profile.TutorInfoScreen
import com.github.se.project.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme { Surface(modifier = Modifier.fillMaxSize()) { PocketTutorApp() } }
    }
  }
}

@Composable
fun PocketTutorApp() {
  // Navigation
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  // View models
  val authenticationViewModel: AuthenticationViewModel = viewModel()

  val listProfilesViewModel: ListProfilesViewModel =
      viewModel(factory = ListProfilesViewModel.Factory)
  val profiles = listProfilesViewModel.profiles

  val lessonViewModel: LessonsViewModel =
      viewModel(factory = LessonsViewModel.Factory(listProfilesViewModel))

  // Google user unique id (as var to be able to pass from the SignIn to CreateProfile screens)
  var googleUid = ""

  // Context
  val context = LocalContext.current

  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication flow
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      composable(Screen.AUTH) {
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
                      // If the user doesn't have a profile, navigate to the profile creation screen
                      navigationActions.navigateTo(Screen.CREATE_PROFILE)
                    }
                  })
            })
      }
      // For debugging purposes (when sign-in error)
      // composable(Screen.AUTH) {
      //   googleUid = "1234"
      //   CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid)
      // }

      composable(Screen.HOME) {
        Greeting("Android") // TODO: Replace with HomeScreen
      }
      composable(Screen.CREATE_PROFILE) {
        CreateProfileScreen(navigationActions, listProfilesViewModel, googleUid)
      }
      composable(Screen.CREATE_TUTOR_PROFILE) {
        TutorInfoScreen(navigationActions, listProfilesViewModel)
      }
      composable(Screen.CREATE_TUTOR_SCHEDULE) {
        AvailabilityScreen(navigationActions, listProfilesViewModel)
      }
      composable(Screen.PROFILE) {
        ProfileInfoScreen(navigationActions, listProfilesViewModel, lessonViewModel)
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}
