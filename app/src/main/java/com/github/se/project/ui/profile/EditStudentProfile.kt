package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Section
import com.github.se.project.ui.navigation.NavigationActions

@Composable
fun EditStudentProfile(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(
              text = "No Profile selected. Should not happen.",
              modifier = Modifier.testTag("editTutorNoProfile"))
  var firstName by remember { mutableStateOf(currentProfile.firstName) }
  var lastName by remember { mutableStateOf(currentProfile.lastName) }
  var phoneNumber by remember { mutableStateOf(currentProfile.phoneNumber) }
  val academicLevel = remember { mutableStateOf(currentProfile.academicLevel.name) }
  var nameEditing by remember { mutableStateOf(false) }
  val section = remember { mutableStateOf(currentProfile.section.name) }
  var expandedSection by remember { mutableStateOf(false) }
  var expandedAcademicLevel by remember { mutableStateOf(false) }
  val context = LocalContext.current

  Scaffold(
      topBar = {
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.testTag("editProfileCloseButton")) {
              Icon(
                  imageVector = Icons.AutoMirrored.Default.ArrowBack,
                  contentDescription = "Go Back")
            }
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(paddingValues)
                    .testTag("editScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                    text = "${currentProfile.firstName} ${currentProfile.lastName}",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("nameTitle"))

                // Edit button with icon
                IconButton(
                    onClick = { nameEditing = !nameEditing },
                    modifier = Modifier.testTag("editNameButton").offset(y = (-4).dp)) {
                      Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
              }
              Text(
                  "Modify your profile information:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.testTag("editProfileInstructionText"))

              Spacer(modifier = Modifier.height(16.dp))

              if (nameEditing) {
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
              }

              // Phone Number input
              OutlinedTextField(
                  value = phoneNumber,
                  onValueChange = { phoneNumber = it },
                  label = { Text("Phone Number") },
                  placeholder = { Text("Enter your phone number") },
                  modifier = Modifier.fillMaxWidth().testTag("phoneNumberField"),
                  shape = MaterialTheme.shapes.small,
                  singleLine = true)

              Text(text = "Modify your section", style = MaterialTheme.typography.titleSmall)

              Spacer(modifier = Modifier.height(16.dp))

              // Section dropdown menu with improved styling
              Box {
                Text(
                    text = section.value, // Directly access the value
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable { expandedSection = true }
                            .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(
                                1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("editProfileSectionDropdown"),
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
                              section.value = s.name
                              expandedSection = false
                            },
                            modifier = Modifier.testTag("editProfileSectionDropdownItem-${s.name}"))
                      }
                    }
              }

              Text(text = "Modify your academic level", style = MaterialTheme.typography.titleSmall)
              Box {
                Text(
                    text = academicLevel.value, // Directly access the value
                    modifier =
                        Modifier.fillMaxWidth()
                            .clickable { expandedAcademicLevel = true }
                            .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(
                                1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("editProfileAcademicLevelDropdown"),
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
                              academicLevel.value = a.name
                              expandedAcademicLevel = false
                            },
                            modifier =
                                Modifier.testTag(
                                    "editTutorProfileAcademicLevelDropdownItem-${a.name}"))
                      }
                    }
              }
            }
      },
      bottomBar = {
        // Confirmation Button with Validation
        Button(
            modifier =
                Modifier.fillMaxWidth().padding(16.dp).testTag("editTutorProfileConfirmButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = {
              if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
              } else if (!isPhoneNumberValid(phoneNumber)) {
                Toast.makeText(context, "Please input a valid phone number", Toast.LENGTH_SHORT)
                    .show()
              } else {
                currentProfile.firstName = firstName
                currentProfile.lastName = lastName
                currentProfile.phoneNumber = phoneNumber
                currentProfile.academicLevel = AcademicLevel.valueOf(academicLevel.value)
                currentProfile.section = Section.valueOf(section.value)

                listProfilesViewModel.updateProfile(currentProfile)
                navigationActions.goBack()

                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
              }
            }) {
              Text("Continue")
            }
      })
}
