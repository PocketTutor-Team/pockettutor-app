package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
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
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.EpflCertification
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.EpflVerificationCard
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    certificationViewModel: CertificationViewModel, // Add this parameter
    googleUid: String
) {
  // State management
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  val section: MutableState<Section?> = remember { mutableStateOf(null) }
  val academicLevel: MutableState<AcademicLevel?> = remember { mutableStateOf(null) }

  var sciper by remember { mutableStateOf("") } // Add SCIPER state

  val context = LocalContext.current
  var token = "" // For Firebase messaging

  // Observe verification state
  val verificationState by certificationViewModel.verificationState.collectAsState()
  val isVerified = verificationState is CertificationViewModel.VerificationState.Success

  // Effect to handle verification result
  LaunchedEffect(verificationState) {
    when (val state = verificationState) {
      is CertificationViewModel.VerificationState.Success -> {
        // Auto-fill form with verified data
        firstName = state.result.firstName
        lastName = state.result.lastName
        section.value = state.result.section
        academicLevel.value = state.result.academicLevel
        Toast.makeText(context, "EPFL Profile verified successfully!", Toast.LENGTH_SHORT).show()
      }
      is CertificationViewModel.VerificationState.Error -> {
        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
      }
      else -> {} // Handle other states if needed
    }
  }

  Scaffold(
      topBar = {
        Text(
            text = "Welcome to Pocket Tutor!",
            style = MaterialTheme.typography.headlineMedium,
            modifier =
                Modifier.padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
                    .fillMaxWidth()
                    .testTag("welcomeText"))
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Make screen scrollable
            verticalArrangement = Arrangement.spacedBy(12.dp)) {

              // EPFL Verification Section
              EpflVerificationCard(
                  sciper = sciper,
                  onSciperChange = { sciper = it },
                  verificationState = verificationState,
                  onVerifyClick = { certificationViewModel.verifySciperNumber(it) },
                  onResetVerification = { certificationViewModel.resetVerification() },
                  modifier = Modifier.fillMaxWidth())

              // Existing profile creation fields
              OutlinedTextField(
                  value = firstName,
                  onValueChange = { firstName = it },
                  label = { Text("First Name") },
                  modifier = Modifier.fillMaxWidth().testTag("firstNameField"),
                  singleLine = true,
                  enabled = !isVerified // Disable if verified
                  )

              OutlinedTextField(
                  value = lastName,
                  onValueChange = { lastName = it },
                  label = { Text("Last Name") },
                  modifier = Modifier.fillMaxWidth().testTag("lastNameField"),
                  singleLine = true,
                  enabled = !isVerified // Disable if verified
                  )

              // Section and Academic Level
              SectionSelector(section, !isVerified)

              AcademicSelector(academicLevel, !isVerified)

              OutlinedTextField(
                  value = phoneNumber,
                  onValueChange = { phoneNumber = it },
                  label = { Text("Phone Number") },
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("phoneNumberField"), // Test tag for phone number input
                  singleLine = true)

              // Role selection
              Column(modifier = Modifier.fillMaxWidth()) {
                Text("You are a:", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth().testTag("roleSelection"),
                ) {
                  listOf(Role.STUDENT, Role.TUTOR).forEach { r ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.baseShape,
                        selected = r == role,
                        onClick = { role = r },
                        colors = SegmentedButtonDefaults.colors(activeContentColor = Color.White),
                        modifier =
                            Modifier.padding(4.dp)
                                .testTag(
                                    "roleButton${if (r == Role.STUDENT) "Student" else "Tutor"}")) {
                          Text(if (r == Role.STUDENT) "Student" else "Tutor")
                        }
                  }
                }
              }

              Spacer(modifier = Modifier.weight(1f))

              // Create profile button
              Button(
                  onClick = {
                    if (validateInputs(
                        firstName,
                        lastName,
                        phoneNumber,
                        role,
                        section.value,
                        academicLevel.value)) {
                      // Create profile with certification if verified
                      val certification =
                          if (verificationState
                              is CertificationViewModel.VerificationState.Success) {
                            EpflCertification(sciper = sciper, verified = true)
                          } else null

                      val newProfile =
                          Profile(
                              uid = listProfilesViewModel.getNewUid(),
                              token = token,
                              googleUid = googleUid,
                              firstName = firstName,
                              lastName = lastName,
                              phoneNumber = phoneNumber,
                              role = role,
                              section = section.value!!,
                              academicLevel = academicLevel.value!!,
                              certification = certification)

                      listProfilesViewModel.addProfile(newProfile)
                      listProfilesViewModel.setCurrentProfile(newProfile)

                      if (role == Role.TUTOR) {
                        navigationActions.navigateTo(Screen.CREATE_TUTOR_PROFILE)
                      } else {
                        navigationActions.navigateTo(Screen.HOME)
                      }
                    } else {
                      Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
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

private fun validateInputs(
    firstName: String,
    lastName: String,
    phoneNumber: String,
    role: Role,
    section: Section?,
    academicLevel: AcademicLevel?
): Boolean {
  return firstName.isNotEmpty() &&
      lastName.isNotEmpty() &&
      phoneNumber.isNotEmpty() &&
      isPhoneNumberValid(phoneNumber) &&
      role != Role.UNKNOWN &&
      section != null &&
      academicLevel != null
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
