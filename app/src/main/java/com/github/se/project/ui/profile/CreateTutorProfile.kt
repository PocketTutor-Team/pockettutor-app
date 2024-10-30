package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.components.LanguageSelector
import com.github.se.project.ui.components.PriceSlider
import com.github.se.project.ui.components.SubjectsSelector
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
                    .testTag("tutorInfoScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  "Complete your profile by selecting your capabilities:",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.testTag("instructionText"))

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                  "What languages do you feel comfortable teaching in?",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("languageText"))
              // Language Selection
              LanguageSelector(selectedLanguages)

              Spacer(modifier = Modifier.height(16.dp))

              // Subject Selection
              Text(
                  "Which subjects do you teach?",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("subjectText"))
              SubjectsSelector(selectedSubjects)

              Spacer(modifier = Modifier.height(16.dp))

              // Price Selection
              Text(
                  "Select your tutoring price per hour:",
                  style = MaterialTheme.typography.titleSmall,
                  modifier = Modifier.testTag("priceText"))
              PriceSlider(sliderValue)
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

                listProfilesViewModel.updateProfile(
                    profile.copy(
                        languages = selectedLanguages.toList(),
                        subjects = selectedSubjects.toList()))
                navigationActions.navigateTo(Screen.CREATE_TUTOR_SCHEDULE)

                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
              }
            }) {
              Text("Continue")
            }
      })
}
