package com.github.se.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.project.resources.C
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Route
import com.github.se.project.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              Greeting("Android")
            }
      }
    }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  SampleAppTheme { Greeting("Android") }
}