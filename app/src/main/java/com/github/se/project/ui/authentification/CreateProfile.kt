package com.github.se.project.ui.authentification

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  // Profile details
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var role by remember { mutableStateOf(Role.UNKNOWN) }
  var section by remember { mutableStateOf("") }
  var academicLevel by remember { mutableStateOf("") }
  val email = "test.default@epfl.ch" // TODO: link to google sign-in

  // Context for the Toast messages
  val context = LocalContext.current

  // Dropdown menu states (for section and academic level selection)
  var expandedSection by remember { mutableStateOf(false) }
  var expandedAcademicLevel by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        Text(
            text = "Welcome on Pocket Tutor!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp))
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  text = "Please enter your details in the following fields:",
                  modifier = Modifier.fillMaxWidth())

              // TODO: profile picture

              // First name input
              OutlinedTextField(
                  value = firstName,
                  onValueChange = { firstName = it },
                  label = { Text("First Name") },
                  placeholder = { Text("Enter your first name") },
                  modifier = Modifier.fillMaxWidth())

              // Last name input
              OutlinedTextField(
                  value = lastName,
                  onValueChange = { lastName = it },
                  label = { Text("Last Name") },
                  placeholder = { Text("Enter your last name") },
                  modifier = Modifier.fillMaxWidth())

              // Role input
              Text("You are a :")
              val roles = listOf(Role.STUDENT, Role.TUTOR)
              SingleChoiceSegmentedButtonRow(
                  modifier = Modifier.fillMaxWidth(),
              ) {
                roles.forEach { r ->
                  Spacer(Modifier.width(5.dp))
                  SegmentedButton(
                      shape = SegmentedButtonDefaults.baseShape,
                      selected = r == role,
                      onClick = { role = r }) {
                        Text(r.name)
                      }
                  Spacer(Modifier.width(5.dp))
                }
              }

              // Section input
              Box {
                OutlinedTextField(
                    value = section,
                    onValueChange = {
                      section = it
                      expandedSection = section.isNotEmpty()
                      // Note: this avoid displaying the dropdown menu with all sections
                    },
                    label = { Text("Your Section") },
                    placeholder = { Text("Select your section") },
                    modifier = Modifier.fillMaxWidth(),
                )

                DropdownMenu(
                    expanded = expandedSection,
                    onDismissRequest = { expandedSection = false },
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    properties = PopupProperties(focusable = false)) {
                      // Display only the sections that corresponds to the user input
                      Section.entries
                          .filter { si ->
                            (si.name.startsWith(section) || si.name.lowercase().startsWith(section))
                          }
                          .forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name) },
                                onClick = {
                                  section = s.name
                                  expandedSection = false
                                })
                          }
                    }
              }

              // Academic level input
              Box {
                OutlinedTextField(
                    value = academicLevel,
                    onValueChange = {
                      academicLevel = it
                      expandedAcademicLevel = academicLevel.isNotEmpty()
                      // Note: this avoid displaying the dropdown menu with all academic levels
                    },
                    label = { Text("Your Academic Level") },
                    placeholder = { Text("Select your academic level") },
                    modifier = Modifier.fillMaxWidth())

                DropdownMenu(
                    expanded = expandedAcademicLevel,
                    onDismissRequest = { expandedAcademicLevel = false },
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    properties = PopupProperties(focusable = false)) {
                      // Display only the academic levels that corresponds to the user input
                      AcademicLevel.entries
                          .filter { al ->
                            (al.name.startsWith(academicLevel) ||
                                al.name.lowercase().startsWith(academicLevel))
                          }
                          .forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.name) },
                                onClick = {
                                  academicLevel = a.name
                                  expandedAcademicLevel = false
                                })
                          }
                    }
              }
            }
      },
      bottomBar = {
        // Create profile button
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = {
              // Create a new profile if all fields are completed
              if (firstName.isNotEmpty() &&
                  lastName.isNotEmpty() &&
                  role != Role.UNKNOWN &&
                  section.isNotEmpty() &&
                  academicLevel.isNotEmpty()) {
                try {
                  listProfilesViewModel.addProfile(
                      Profile(
                          listProfilesViewModel.getNewUid(),
                          firstName,
                          lastName,
                          role,
                          Section.valueOf(section),
                          AcademicLevel.valueOf(academicLevel),
                          email))
                } catch (e: IllegalArgumentException) {
                  Toast.makeText(
                          context,
                          "Please select a section and an academic level from the dropdown menu!",
                          Toast.LENGTH_SHORT)
                      .show()
                  return@Button
                }
              }

              Toast.makeText(
                      context,
                      "Please complete all the fields before creating your account!",
                      Toast.LENGTH_SHORT)
                  .show()
            }) {
              Text("Confirm your details")
            }
      })
}
