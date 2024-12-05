package com.github.se.project.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    googleUid: String
) {

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  val section: MutableState<Section?> = remember { mutableStateOf(null) }
  val academicLevel: MutableState<AcademicLevel?> = remember { mutableStateOf(null) }
  val context = LocalContext.current
  var token = ""

  Scaffold(
      topBar = {
        Text(
            text = "Welcome to Pocket Tutor!",
            style = MaterialTheme.typography.headlineMedium,
            modifier =
                Modifier.padding(vertical = 16.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .testTag("welcomeText") // Test tag for top title text
            )
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Text(
                  text = "Please enter your details to create your profile:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(bottom = 8.dp)
                          .testTag("instructionText") // Test tag for instruction text
                  )

              OutlinedTextField(
                  value = firstName,
                  onValueChange = {
                    if (it.length <= context.resources.getInteger(R.integer.user_first_name)) {
                      Log.d("CreateProfileScreen", "firstName length: ${it.length}")
                      firstName = it
                    }
                  },
                  label = { Text("First Name") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("firstNameField"), // Test tag for first name input
                  singleLine = true)

              OutlinedTextField(
                  value = lastName,
                  onValueChange = {
                    if (it.length <= context.resources.getInteger(R.integer.user_last_name)) {
                      Log.d("CreateProfileScreen", "lastName length: ${it.length}")
                      lastName = it
                    }
                  },
                  label = { Text("Last Name") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("lastNameField"), // Test tag for last name input
                  singleLine = true)

              OutlinedTextField(
                  value = phoneNumber,
                  onValueChange = { phoneNumber = it },
                  label = { Text("Phone Number") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("phoneNumberField"), // Test tag for phone number input
                  singleLine = true)

              Column(modifier = Modifier.fillMaxWidth()) {
                Text("You are a:", style = MaterialTheme.typography.titleSmall)
                val roles = listOf(Role.STUDENT, Role.TUTOR)
                SingleChoiceSegmentedButtonRow(
                    modifier =
                        Modifier.fillMaxWidth()
                            .testTag("roleSelection") // Test tag for role selection row
                    ) {
                      roles.forEach { r ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.baseShape,
                            selected = r == role,
                            onClick = { role = r },
                            colors =
                                SegmentedButtonDefaults.colors(activeContentColor = Color.White),
                            modifier =
                                Modifier.padding(4.dp)
                                    .testTag(
                                        "roleButton${if (r == Role.STUDENT) "Student" else "Tutor"}") // Test tag for role buttons
                            ) {
                              Text(
                                  text = if (r == Role.STUDENT) "Student" else "Tutor",
                                  style = MaterialTheme.typography.labelLarge)
                            }
                      }
                    }
              }

              Text(text = "Select your section", style = MaterialTheme.typography.titleSmall)
              // Section dropdown menu with improved styling
              SectionSelector(section)

              Text(text = "Select your academic level", style = MaterialTheme.typography.titleSmall)
              // Academic Level dropdown menu with improved styling
              AcademicSelector(academicLevel)

              Spacer(modifier = Modifier.weight(1f))

              FirebaseMessaging.getInstance()
                  .token
                  .addOnCompleteListener(
                      OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                          // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                          return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        token = task.result

                        // Log and toast
                        val msg = "FCM Token: $token"
                        // Log.d(TAG, msg)

                        // Implement this method to send token to your app server.
                        // Log.d("MainActivity" ,"sendRegistrationTokenToServer($token)")
                      })

              Button(
                  onClick = {
                    if (firstName.isNotEmpty() &&
                        lastName.isNotEmpty() &&
                        phoneNumber.isNotEmpty() &&
                        isPhoneNumberValid(phoneNumber) &&
                        role != Role.UNKNOWN &&
                        section.value != null &&
                        academicLevel.value != null) {
                      try {
                        val newProfile =
                            Profile(
                                listProfilesViewModel.getNewUid(),
                                token,
                                googleUid,
                                firstName,
                                lastName,
                                phoneNumber,
                                role,
                                section.value!!,
                                academicLevel.value!!)
                        listProfilesViewModel.setCurrentProfile(newProfile)

                        if (role == Role.TUTOR) {
                          navigationActions.navigateTo(Screen.CREATE_TUTOR_PROFILE)
                        } else if (role == Role.STUDENT) {
                          listProfilesViewModel.addProfile(newProfile)
                          navigationActions.navigateTo(Screen.HOME)
                        }
                      } catch (e: Exception) {
                        Toast.makeText(
                                context,
                                "An error occurred. Please check your inputs and try again.",
                                Toast.LENGTH_SHORT)
                            .show()
                      }
                    } else {
                      Toast.makeText(
                              context,
                              "Please fill all fields with valid information!",
                              Toast.LENGTH_SHORT)
                          .show()
                    }
                  },
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(48.dp)
                          .testTag("confirmButton"), // Test tag for confirm button
                  shape = MaterialTheme.shapes.medium) {
                    Text("Confirm your details")
                  }
            }
      }
}

/**
 * Checks if the phone number is valid. To be valid a phone number must be composed of 10 to 15
 * digits with an optional '+' at the beginning and no other characters.
 *
 * @param phoneNumber The phone number to check.
 * @return true if the phone number is valid, false otherwise.
 */
internal fun isPhoneNumberValid(phoneNumber: String): Boolean {
  val phoneNumberRegex = "^\\+?[0-9]{10,15}\$".toRegex()
  return phoneNumber.matches(phoneNumberRegex)
}
