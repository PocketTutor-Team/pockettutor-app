package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions

@Composable
fun EditProfile(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val profile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(
              text = "No Profile selected. Should not happen.",
              modifier = Modifier.testTag("editTutorNoProfile"))

  val profileLanguages = remember { mutableStateListOf<Language>() }
  val profileSubjects = remember { mutableStateListOf<Subject>() }

  LaunchedEffect(profile) {
    profileLanguages.clear()
    profileLanguages.addAll(profile.languages)

    profileSubjects.clear()
    profileSubjects.addAll(profile.subjects)
  }

  var firstName by remember { mutableStateOf(profile.firstName) }
  var lastName by remember { mutableStateOf(profile.lastName) }
  var phoneNumber by remember { mutableStateOf(profile.phoneNumber) }

  val priceSliderValue = remember { mutableFloatStateOf(profile.price.toFloat()) }
  val showError = remember { mutableStateOf(false) }

  val academicLevel = remember { mutableStateOf(profile.academicLevel.name) }
  val section = remember { mutableStateOf(profile.section.name) }
  var expandedSection by remember { mutableStateOf(false) }
  var expandedAcademicLevel by remember { mutableStateOf(false) }
  var nameEditing by remember { mutableStateOf(false) }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.testTag("editTutorProfileCloseButton")) {
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
                    .testTag("tutorInfoScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                    text = "${profile.firstName} ${profile.lastName}",
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
                  modifier = Modifier.testTag("editTutorProfileInstructionText"))

              Spacer(modifier = Modifier.height(6.dp))

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

            if(profile.role == Role.TUTOR) {
                Text(
                    "Teaching languages:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfileLanguageText")
                )
                // Language Selection
                LanguageSelector(profileLanguages)

                Spacer(modifier = Modifier.height(6.dp))

                // Subject Selection
                Text(
                    "Teaching subjects:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfileSubjectText")
                )
                SubjectSelector(null, profileSubjects, true)

                Spacer(modifier = Modifier.height(6.dp))

                // Price Selection
                Text(
                    "Tutoring price per hour:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfilePriceText")
                )
                PriceSlider(priceSliderValue)

                Spacer(modifier = Modifier.height(5.dp))
            }

              Text(text = "Modify your section", style = MaterialTheme.typography.titleSmall)
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
                            .testTag("editTutorProfileSectionDropdown"),
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
                            modifier =
                                Modifier.testTag("editTutorProfileSectionDropdownItem-${s.name}"))
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
                            .testTag("editTutorProfileAcademicLevelDropdown"),
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
              if (profile.role == Role.TUTOR && (profileLanguages.isEmpty() || profileSubjects.isEmpty())) {
                showError.value = true
                Toast.makeText(
                        context,
                        "Please select at least one language and one subject",
                        Toast.LENGTH_SHORT)
                    .show()
              } else if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                showError.value = true
                Toast.makeText(context, "Please fill in all the text fields", Toast.LENGTH_SHORT)
                    .show()
              } else if (!isPhoneNumberValid(phoneNumber)) {
                showError.value = true
                Toast.makeText(context, "Please input a valid phone number", Toast.LENGTH_SHORT)
                    .show()
              } else {
                showError.value = false
                profile.firstName = firstName
                profile.lastName = lastName
                profile.phoneNumber = phoneNumber
                profile.languages = profileLanguages
                profile.subjects = profileSubjects
                profile.price = priceSliderValue.floatValue.toInt()
                profile.academicLevel =
                    AcademicLevel.valueOf(
                        academicLevel.value) // Adjust based on your enum definition
                profile.section = Section.valueOf(section.value)

                listProfilesViewModel.updateProfile(profile)
                navigationActions.goBack()

                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
              }
            }) {
              Text("Continue")
            }
      })
}
