package com.github.se.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.github.se.project.model.profile.*
import com.github.se.project.resources.C
import com.github.se.project.ui.authentication.AvailabilityScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        // Initialize the profile state here
        var profile by remember {
          mutableStateOf(
              Profile(
                  uid = "12345",
                  firstName = "John",
                  lastName = "Doe",
                  role = Role.TUTOR,
                  section = Section.IN,
                  academicLevel = AcademicLevel.MA2,
                  email = "john.doe@example.com",
                  schedule =
                      List(7) { List(12) { 0 } } // Initialize empty schedule (7 days x 12 slots)
                  ))
        }

        // Pass the profile to AvailabilityScreen and handle profile updates
        AvailabilityScreen(
            profile = profile,
            onProfileUpdate = { updatedProfile ->
              profile = updatedProfile // Update the profile state when schedule changes
            })
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
fun DefaultPreview() {
  val initialProfile =
      Profile(
          uid = "12345",
          firstName = "John",
          lastName = "Doe",
          role = Role.TUTOR,
          section = Section.IN,
          academicLevel = AcademicLevel.MA2,
          email = "john.doe@example.com",
          schedule = List(7) { List(12) { 0 } } // Initial empty schedule
          )

  MaterialTheme {
    AvailabilityScreen(profile = initialProfile, onProfileUpdate = { /* Handle profile update */})
  }
}
