package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.profile.AcademicLevel
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.model.profile.Section
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.AcademicSelector
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PhoneNumberInput
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.components.SectionSelector
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.components.isPhoneNumberValid
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.countries

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

  var phoneNumber by remember { mutableStateOf(profile.phoneNumber.drop(3)) }
  var selectedCountry by remember {
    mutableStateOf(countries.find { it.code == profile.phoneNumber.take(3) } ?: countries[0])
  }

  val priceSliderValue = remember { mutableFloatStateOf(profile.price.toFloat()) }
  val showError = remember { mutableStateOf(false) }

  val academicLevel: MutableState<AcademicLevel?> = remember {
    mutableStateOf(profile.academicLevel)
  }
  val section: MutableState<Section?> = remember { mutableStateOf(profile.section) }

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

              // Phone Number Input
              Text(text = "Phone Number", style = MaterialTheme.typography.titleSmall)
              PhoneNumberInput(
                  selectedCountry = selectedCountry,
                  onCountryChange = { selectedCountry = it },
                  phoneNumber = phoneNumber,
                  onPhoneNumberChange = { phoneNumber = it })

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
                PriceSlider(priceSliderValue, listProfilesViewModel.getAveragePrice())

                Button(
                    modifier = Modifier.fillMaxWidth().testTag("updateAvailabilityButton"),
                    shape = MaterialTheme.shapes.medium,
                    onClick = { navigationActions.navigateTo(Screen.EDIT_SCHEDULE) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer)) {
                      Icon(
                          imageVector = Icons.Default.DateRange,
                          contentDescription = "Calendar",
                          tint = MaterialTheme.colorScheme.onPrimary,
                          modifier = Modifier.padding(end = 16.dp))
                      Text(
                          stringResource(id = R.string.schedule),
                          color = MaterialTheme.colorScheme.onPrimary)
                    }
              }

              if (profile.certification?.verified == false) {
                Text(text = "Modify your section", style = MaterialTheme.typography.titleSmall)
                SectionSelector(section)

                Text(
                    text = "Modify your academic level",
                    style = MaterialTheme.typography.titleSmall)
                AcademicSelector(academicLevel)
              }
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
              } else if (!isPhoneNumberValid(selectedCountry.code, phoneNumber)) {
                showError.value = true
                Toast.makeText(context, "Please input a valid phone number", Toast.LENGTH_SHORT)
                    .show()
              } else {
                showError.value = false
                profile.phoneNumber = selectedCountry.code + phoneNumber
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
              Text(stringResource(id = R.string.update_profile))
            }
      })
}
