package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

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

  var phoneNumber by remember { mutableStateOf(profile.phoneNumber) }

  val priceSliderValue = remember { mutableFloatStateOf(profile.price.toFloat()) }
  val showError = remember { mutableStateOf(false) }

  val academicLevel: MutableState<AcademicLevel?> = remember {
    mutableStateOf(profile.academicLevel)
  }
  val section: MutableState<Section?> = remember { mutableStateOf(profile.section) }
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
                    .testTag("tutorInfoScreen")
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  "Modify your profile information:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.testTag("editTutorProfileInstructionText"))

              Spacer(modifier = Modifier.height(6.dp))

              // Phone Number input
              OutlinedTextField(
                  value = phoneNumber,
                  onValueChange = { phoneNumber = it },
                  label = { Text("Phone Number") },
                  placeholder = { Text("Enter your phone number") },
                  modifier = Modifier.fillMaxWidth().testTag("phoneNumberField"),
                  shape = MaterialTheme.shapes.small,
                  singleLine = true)

              if (profile.role == Role.TUTOR) {
                Text(
                    "Teaching languages:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfileLanguageText"))
                // Language Selection
                LanguageSelector(profileLanguages)

                Spacer(modifier = Modifier.height(6.dp))

                // Subject Selection
                Text(
                    "Teaching subjects:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfileSubjectText"))
                SubjectSelector(null, profileSubjects, true)

                Spacer(modifier = Modifier.height(6.dp))

                // Price Selection
                Text(
                    "Tutoring price per hour:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("editTutorProfilePriceText"))
                PriceSlider(priceSliderValue)

                // Spacer(modifier = Modifier.height(5.dp))
              }
              Button(
                  modifier =
                      Modifier.fillMaxWidth().padding(16.dp).testTag("updateAvailabilityButton"),
                  shape = MaterialTheme.shapes.medium,
                  onClick = { navigationActions.navigateTo(Screen.EDIT_SCHEDULE) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.secondaryContainer,
                          contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(end = 16.dp))
                    Text(stringResource(id = R.string.schedule))
                  }

              Text(text = "Modify your section", style = MaterialTheme.typography.titleSmall)
              SectionSelector(section)

              Text(text = "Modify your academic level", style = MaterialTheme.typography.titleSmall)
              AcademicSelector(academicLevel)
            }
      },
      bottomBar = {
        // Confirmation Button with Validation
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = {
              if (profile.role == Role.TUTOR &&
                  (profileLanguages.isEmpty() || profileSubjects.isEmpty())) {
                showError.value = true
                Toast.makeText(
                        context,
                        "Please select at least one language and one subject",
                        Toast.LENGTH_SHORT)
                    .show()
              } else if (!isPhoneNumberValid(phoneNumber)) {
                showError.value = true
                Toast.makeText(context, "Please input a valid phone number", Toast.LENGTH_SHORT)
                    .show()
              } else {
                showError.value = false
                profile.phoneNumber = phoneNumber
                profile.languages = profileLanguages
                profile.subjects = profileSubjects
                profile.price = priceSliderValue.floatValue.toInt()
                profile.academicLevel = academicLevel.value!!
                profile.section = section.value!!

                listProfilesViewModel.updateProfile(profile)
                navigationActions.goBack()

                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
              }
            }) {
              Text(stringResource(id = R.string.update_profil))
            }
      })
}
