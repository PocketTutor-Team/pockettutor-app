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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Subject
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.components.SubjectSelector
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTutorProfile(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory)
) {
  val profile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(
              text = "No Profile selected. Should not happen.",
              color = Color.Red,
              modifier = Modifier.testTag("noProfile"))

  val selectedLanguages = remember { mutableStateListOf<Language>() }
  val selectedSubjects = remember { mutableStateListOf<Subject>() }
  val sliderValue = remember { mutableFloatStateOf(5f) }
  val showError = remember { mutableStateOf(false) }
  var description by remember { mutableStateOf("") }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        Text(
            text = "Welcome Tutor!",
            modifier =
                Modifier.padding(vertical = 32.dp, horizontal = 16.dp).testTag("welcomeText"),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center)
      },
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(paddingValues)
                    .testTag("tutorInfoScreen")
                    .verticalScroll(rememberScrollState()), // Make screen scrollable
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  "Complete your profile by selecting your capabilities:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.testTag("instructionText"))

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                  "What languages do you feel comfortable teaching in?",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("languageText"))
              // Language Selection
              LanguageSelector(selectedLanguages)

              Spacer(modifier = Modifier.height(12.dp))

              // Subject Selection
              Text(
                  "Which subjects do you teach?",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("subjectText"))
              SubjectSelector(null, selectedSubjects, true)

              Spacer(modifier = Modifier.height(12.dp))

              // Price Selection
              Text(
                  "Select your tutoring price per hour:",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("priceText"))
              PriceSlider(sliderValue, listProfilesViewModel.getAveragePrice())

              // Description text field
              OutlinedTextField(
                  value = description,
                  onValueChange = {
                    if (it.length <= context.resources.getInteger(R.integer.description)) {
                      description = it
                    }
                  },
                  modifier =
                      Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("experienceField"),
                  label = { Text("Do you have any experience as a tutor ?") },
                  placeholder = {
                    Text(
                        "Share your previous tutoring experience, or if you don't have any, explain why you would be a great tutor.")
                  },
                  shape = MaterialTheme.shapes.medium,
                  maxLines = 4,
                  keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done))
            }
      },
      bottomBar = {
        // Confirmation Button with Validation
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
            shape = MaterialTheme.shapes.medium,
            onClick = {
              if (selectedSubjects.isEmpty() || selectedLanguages.isEmpty()) {
                showError.value = true
                Toast.makeText(
                        context,
                        "Please select at least one language and one subject",
                        Toast.LENGTH_SHORT)
                    .show()
              } else {
                showError.value = false
                profile.price = sliderValue.floatValue.toInt()

                val updatedProfile =
                    profile.copy(
                        languages = selectedLanguages.toList(),
                        subjects = selectedSubjects.toList(),
                        price = sliderValue.floatValue.toInt(),
                        description = description)

                listProfilesViewModel.setCurrentProfile(updatedProfile)

                navigationActions.navigateTo(Screen.CREATE_TUTOR_SCHEDULE)

                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
              }
            }) {
              Text("Continue")
            }
      })
}
