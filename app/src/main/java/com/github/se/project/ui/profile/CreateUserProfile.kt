package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestoreException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    googleUid: String
) {
  // Profile details
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  var section by remember { mutableStateOf("") }
  var academicLevel by remember { mutableStateOf("") }

  // Context for the Toast messages
  val context = LocalContext.current

  // Dropdown menu states (for section and academic level selection)
  var expandedSection by remember { mutableStateOf(false) }
  var expandedAcademicLevel by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        Text(
            text = "Welcome to Pocket Tutor!",
            style = MaterialTheme.typography.headlineMedium,
            modifier =
                Modifier.padding(vertical = 32.dp, horizontal = 16.dp).testTag("welcomeText"),
            textAlign = TextAlign.Center)
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Spacer(modifier = Modifier.height(8.dp))

              Text(
                  text = "Please enter your details to create your profile:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.fillMaxWidth().testTag("instructionText"))

              Spacer(modifier = Modifier.height(4.dp))

              // First Name input
              OutlinedTextField(
                  value = firstName,
                  onValueChange = { firstName = it },
                  textStyle = MaterialTheme.typography.bodyLarge,
                  label = { Text("First Name") },
                  placeholder = { Text("Enter your first name") },
                  modifier = Modifier.fillMaxWidth().testTag("firstNameField"),
                  singleLine = true)

              // Last Name input
              OutlinedTextField(
                  value = lastName,
                  onValueChange = { lastName = it },
                  label = { Text("Last Name") },
                  placeholder = { Text("Enter your last name") },
                  modifier = Modifier.fillMaxWidth().testTag("lastNameField"),
                  shape = MaterialTheme.shapes.small,
                  singleLine = true)

              // Phone Number input
              OutlinedTextField(
                  value = phoneNumber,
                  onValueChange = { phoneNumber = it },
                  label = { Text("Phone Number") },
                  placeholder = { Text("Enter your phone number") },
                  modifier = Modifier.fillMaxWidth().testTag("phoneNumberField"),
                  shape = MaterialTheme.shapes.small,
                  singleLine = true)

              Spacer(modifier = Modifier.height(8.dp))

              // Role selection
              Text("You are a:", style = MaterialTheme.typography.titleSmall)

              val roles = listOf(Role.STUDENT, Role.TUTOR)

              SingleChoiceSegmentedButtonRow(
                  modifier = Modifier.fillMaxWidth().testTag("roleSelection"),
              ) {
                roles.forEach { r ->
                  SegmentedButton(
                      shape = SegmentedButtonDefaults.baseShape,
                      selected = r == role,
                      onClick = { role = r },
                      modifier =
                          Modifier.padding(4.dp)
                              .testTag(
                                  if (r.name == "STUDENT") "roleButtonStudent"
                                  else "roleButtonTutor"),
                      colors =
                          SegmentedButtonDefaults.colors(
                              activeContentColor = MaterialTheme.colorScheme.onPrimary,
                          )) {
                        Text(
                            if (r.name == "STUDENT") "Student" else "Tutor",
                            style = MaterialTheme.typography.labelLarge)
                      }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(text = "Select your section", style = MaterialTheme.typography.titleSmall)
              // Section dropdown menu with improved styling
              Box {
                Text(
                    text = if (section.isNotEmpty()) section else "Section",
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable { expandedSection = true }
                            .background(
                                color = Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(
                                1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("sectionDropdown"),
                    style = MaterialTheme.typography.bodyLarge)

                DropdownMenu(
                    expanded = expandedSection,
                    onDismissRequest = { expandedSection = false },
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    properties = PopupProperties(focusable = true)) {
                      Section.entries.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s.name, style = MaterialTheme.typography.bodyMedium) },
                            onClick = {
                              section = s.name
                              expandedSection = false
                            },
                            modifier = Modifier.testTag("sectionDropdownItem-${s.name}"))
                      }
                    }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(text = "Select your academic level", style = MaterialTheme.typography.titleSmall)
              // Academic Level dropdown menu with improved styling
              Box {
                Text(
                    text = if (academicLevel.isNotEmpty()) academicLevel else "Academic level",
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable { expandedAcademicLevel = true }
                            .background(
                                color = Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(
                                1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("academicLevelDropdown"),
                    style = MaterialTheme.typography.bodyLarge)

                DropdownMenu(
                    expanded = expandedAcademicLevel,
                    onDismissRequest = { expandedAcademicLevel = false },
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    properties = PopupProperties(focusable = true)) {
                      AcademicLevel.entries.forEach { a ->
                        DropdownMenuItem(
                            text = { Text(a.name, style = MaterialTheme.typography.bodyMedium) },
                            onClick = {
                              academicLevel = a.name
                              expandedAcademicLevel = false
                            },
                            modifier = Modifier.testTag("academicLevelDropdownItem-${a.name}"))
                      }
                    }
              }
            }
      },
      bottomBar = {
        // Create profile button
        Button(
            modifier =
                Modifier.fillMaxWidth().padding(16.dp).height(48.dp).testTag("confirmButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = {
              if (firstName.isNotEmpty() &&
                  lastName.isNotEmpty() &&
                  phoneNumber.isNotEmpty() &&
                  isPhoneNumberValid(phoneNumber) &&
                  role != Role.UNKNOWN &&
                  section.isNotEmpty() &&
                  academicLevel.isNotEmpty()) {

                try {

                  val newProfile =
                      Profile(
                          listProfilesViewModel.getNewUid(),
                          googleUid,
                          firstName,
                          lastName,
                          phoneNumber,
                          role,
                          Section.valueOf(section),
                          AcademicLevel.valueOf(academicLevel))

                  listProfilesViewModel.addProfile(newProfile)
                  listProfilesViewModel.setCurrentProfile(newProfile)

                  if (role == Role.TUTOR) {
                    navigationActions.navigateTo(Screen.CREATE_TUTOR_PROFILE)
                  } else if (role == Role.STUDENT) {
                    navigationActions.navigateTo(Screen.HOME)
                  }

                  return@Button
                } catch (e: IllegalArgumentException) {
                  Toast.makeText(
                          context,
                          "Please select a section and an academic level from the dropdown menu!",
                          Toast.LENGTH_SHORT)
                      .show()
                  return@Button
                } catch (e: FirebaseFirestoreException) {
                  Toast.makeText(
                          context,
                          "An error with Firestore occurred while creating your profile, please try again later!",
                          Toast.LENGTH_SHORT)
                      .show()
                  return@Button
                } catch (e: Exception) {
                  Toast.makeText(
                          context,
                          "An unexpected error occurred while creating your profile, please try again later!",
                          Toast.LENGTH_SHORT)
                      .show()
                  return@Button
                }
              }

              if (phoneNumber.isNotEmpty() && isPhoneNumberValid(phoneNumber)) {
                Toast.makeText(
                        context,
                        "Please complete all the fields before creating your account!",
                        Toast.LENGTH_SHORT)
                    .show()
              } else {
                Toast.makeText(context, "Please enter a valid phone number!", Toast.LENGTH_SHORT)
                    .show()
              }
            }) {
              Text("Confirm your details")
            }
      })
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
