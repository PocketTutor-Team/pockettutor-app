package com.github.se.project.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

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
  var expandedSection by remember { mutableStateOf(false) }
  var expandedAcademicLevel by remember { mutableStateOf(false) }

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
                  onValueChange = { firstName = it },
                  label = { Text("First Name") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("firstNameField"), // Test tag for first name input
                  singleLine = true)

              OutlinedTextField(
                  value = lastName,
                  onValueChange = { lastName = it },
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

              Button(
                  onClick = {
                      Log.e("abagaga", "first")
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
                                googleUid,
                                firstName,
                                lastName,
                                phoneNumber,
                                role,
                                section.value!!,
                                academicLevel.value!!)
                        listProfilesViewModel.addProfile(newProfile)
                        listProfilesViewModel.setCurrentProfile(newProfile)

                        if (role == Role.TUTOR) {
                          navigationActions.navigateTo(Screen.CREATE_TUTOR_PROFILE)
                        } else if (role == Role.STUDENT) {
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
