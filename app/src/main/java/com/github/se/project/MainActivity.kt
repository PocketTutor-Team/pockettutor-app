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
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.resources.C
import com.github.se.project.ui.authentification.SignInScreen
import com.github.se.project.ui.profile.CreateProfileScreen
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.ui.theme.SampleAppTheme
import com.github.se.project.model.authentification.AuthenticationViewModel

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

  val authenticationViewModel: AuthenticationViewModel = viewModel()

  val listProfilesViewModel: ListProfilesViewModel =
      viewModel(factory = ListProfilesViewModel.Factory)

  val context = LocalContext.current

  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication flow
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      composable(Screen.AUTH) {
        SignInScreen(
            onSignInClick = {
              authenticationViewModel.handleGoogleSignIn(
                  context,
                  onSuccess = {
                    // On successful sign-in, navigate to the home screen
                    navigationActions.navigateTo(Screen.CREATE_PROFILE)
                  })
            })
      }
      composable(Screen.CREATE_PROFILE) {
        CreateProfileScreen(navigationActions, listProfilesViewModel)
      }
      composable(Screen.HOME) { Greeting("Android") }
    }

    // Home Screen or other screens after sign-in
    // composable(Route.HOME) {
    //      HomeScreen(navController) // Implement your home screen or other navigation destinations
    // }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

  /* UNCOMMENT WHEN IMPLEMENTING SCREENS
  // Navigation
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  NavHost(navController = navController, startDestination = Route.WELCOME) {
    // Add dependencies when creating a screen
    composable(Route.WELCOME) {  }
  } */

  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}
