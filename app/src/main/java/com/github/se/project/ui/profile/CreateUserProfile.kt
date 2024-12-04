package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.PhoneNumberInput
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.components.isPhoneNumberValid
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.countries
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    testMode: Boolean
) {
  // Existing states
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  val section: MutableState<Section?> = remember { mutableStateOf(null) }
  val academicLevel: MutableState<AcademicLevel?> = remember { mutableStateOf(null) }
  val context = LocalContext.current
  var token = ""

  // New states for country code and phone number
  var selectedCountry by remember { mutableStateOf(countries[0]) }
  var phoneNumber by remember { mutableStateOf("") }

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

              // Role Selection
              Column(modifier = Modifier.fillMaxWidth()) {

                // Phone Number Input
                Text(text = "Phone Number", style = MaterialTheme.typography.titleSmall)
                PhoneNumberInput(
                    selectedCountry = selectedCountry,
                    onCountryChange = { selectedCountry = it },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it })

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
              SectionSelector(section)

              Text(text = "Select your academic level", style = MaterialTheme.typography.titleSmall)
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
                        isPhoneNumberValid(selectedCountry.code, phoneNumber) &&
                        role != Role.UNKNOWN &&
                        section.value != null &&
                        academicLevel.value != null) {
                      try {
                        val googleUid: String =
                            if (testMode) {
                              "testingUid"
                            } else {
                              FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            }

                        val newProfile =
                            Profile(
                                listProfilesViewModel.getNewUid(),
                                token,
                                googleUid,
                                firstName,
                                lastName,
                                selectedCountry.code + phoneNumber,
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
