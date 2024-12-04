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
import com.github.se.project.ui.components.PhoneNumberInput
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.components.isPhoneNumberValid
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.countries
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    certificationViewModel: CertificationViewModel, // Add this parameter
    testMode: Boolean
) {
  // State management
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  val section: MutableState<Section?> = remember { mutableStateOf(null) }
  val academicLevel: MutableState<AcademicLevel?> = remember { mutableStateOf(null) }
  var token = ""

  var sciper by remember { mutableStateOf("") } // Add SCIPER state

  val context = LocalContext.current

  // Observe verification state
  val verificationState by certificationViewModel.verificationState.collectAsState()
  val isVerified = verificationState is CertificationViewModel.VerificationState.Success

  // New states for country code and phone number
  var selectedCountry by remember { mutableStateOf(countries[0]) }
  var phoneNumber by remember { mutableStateOf("") }

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

              // Phone Number Input
              Text(text = "Phone Number", style = MaterialTheme.typography.titleSmall)
              PhoneNumberInput(
                  selectedCountry = selectedCountry,
                  onCountryChange = { selectedCountry = it },
                  phoneNumber = phoneNumber,
                  onPhoneNumberChange = { phoneNumber = it })

              // Role Selection
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

                        val certification =
                            if (verificationState
                                is CertificationViewModel.VerificationState.Success) {
                              EpflCertification(sciper = sciper, verified = true)
                            } else null

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
                                academicLevel.value!!,
                                certification = certification)
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
