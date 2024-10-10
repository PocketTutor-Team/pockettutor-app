package com.android.sample

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
import com.android.project.model.profile.AcademicLevel
import com.android.project.model.profile.Profile
import com.android.project.model.profile.Role
import com.android.project.model.profile.Section
import com.android.project.ui.authentication.AvailabilityScreen
import com.android.sample.resources.C
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              ProfilePreview()
            }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    val initialProfile =
        Profile(
            uid = "12345",
            firstName = "John",
            lastName = "Doe",
            role = Role.TUTOR,
            section = Section.IN,
            academicLevel = AcademicLevel.MA2,
            email = "john.doe@example.com",
            schedule = List(7) { List(12) { 1 } } // Initial empty schedule
        )
    MaterialTheme {
        AvailabilityScreen(profile = initialProfile, onProfileUpdate = { /* Handle profile update */})
    }
}
