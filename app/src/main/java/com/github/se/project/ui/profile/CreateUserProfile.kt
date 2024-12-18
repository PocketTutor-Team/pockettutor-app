package com.github.se.project.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.certification.CertificationViewModel
import com.github.se.project.model.certification.EpflCertification
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.EpflVerificationCard
import com.github.se.project.ui.components.PhoneNumberInput
import com.github.se.project.ui.components.ProfilePhotoSelector
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.components.isPhoneNumberValid
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.StorageManager
import com.github.se.project.utils.capitalizeFirstLetter
import com.github.se.project.utils.countries
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

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
  val uid = listProfilesViewModel.getNewUid()
  var localPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  val section: MutableState<Section?> = remember { mutableStateOf(null) }
  val academicLevel: MutableState<AcademicLevel?> = remember { mutableStateOf(null) }
  var token = ""
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }

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

              Text(
                  text = "Add a profile picture (Optional)",
                  style = MaterialTheme.typography.titleSmall)

              ProfilePhotoSelector(
                  currentPhotoUrl = null, onLocalPhotoSelected = { uri -> localPhotoUri = uri })

              // Existing profile creation fields
              OutlinedTextField(
                  value = firstName,
                  onValueChange = {
                    if (it.length <= context.resources.getInteger(R.integer.user_first_name)) {
                      firstName = it
                    }
                  },
                  label = { Text("First Name") },
                  modifier = Modifier.fillMaxWidth().testTag("firstNameField"),
                  singleLine = true,
                  enabled = !isVerified // Disable if verified
                  )

              OutlinedTextField(
                  value = lastName,
                  onValueChange = {
                    if (it.length <= context.resources.getInteger(R.integer.user_last_name)) {
                      lastName = it
                    }
                  },
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
                      isLoading = true
                      coroutineScope.launch {
                        try {
                          val googleUid: String =
                              if (testMode) "testingUid"
                              else FirebaseAuth.getInstance().currentUser?.uid ?: ""

                          val certification =
                              if (verificationState
                                  is CertificationViewModel.VerificationState.Success) {
                                EpflCertification(sciper = sciper, verified = true)
                              } else null

                          val uploadedPhotoUrl: Uri? =
                              localPhotoUri?.let {
                                try {
                                  StorageManager.uploadProfilePhoto(it, uid, context)
                                } catch (e: Exception) {
                                  Toast.makeText(
                                          context,
                                          "Photo upload failed: ${e.message}",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                  null
                                }
                              }

                          val newProfile =
                              Profile(
                                  uid = uid,
                                  token = token,
                                  googleUid = googleUid,
                                  firstName = firstName.capitalizeFirstLetter(),
                                  lastName = lastName.capitalizeFirstLetter(),
                                  phoneNumber = selectedCountry.code + phoneNumber,
                                  role = role,
                                  section = section.value!!,
                                  academicLevel = academicLevel.value!!,
                                  certification = certification,
                                  profilePhotoUrl = uploadedPhotoUrl)

                          listProfilesViewModel.setCurrentProfile(newProfile)

                          if (role == Role.TUTOR)
                              navigationActions.navigateTo(Screen.CREATE_TUTOR_PROFILE)
                          else if (role == Role.STUDENT) {
                            listProfilesViewModel.addProfile(newProfile)
                            navigationActions.navigateTo(Screen.HOME)
                          }

                          Toast.makeText(
                                  context, "Profile created successfully!", Toast.LENGTH_SHORT)
                              .show()
                        } catch (e: Exception) {
                          Toast.makeText(
                                  context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT)
                              .show()
                        } finally {
                          isLoading = false
                        }
                      }
                    } else {
                      Toast.makeText(
                              context,
                              "Please fill all fields with valid information!",
                              Toast.LENGTH_SHORT)
                          .show()
                    }
                  },
                  enabled = !isLoading, // Disable button when loading
                  modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    if (isLoading) {
                      CircularProgressIndicator(
                          modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                      Text("Confirm your details")
                    }
                  }
            }
      }
}
