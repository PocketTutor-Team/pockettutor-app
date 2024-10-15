package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.profile.*
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTutorProfile(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel = viewModel(factory = ListProfilesViewModel.Factory)
) {
    val profile = listProfilesViewModel.currentProfile.collectAsState().value
        ?: return Text(text = "No Profile selected. Should not happen.", color = Color.Red)

    val selectedLanguages = remember { mutableStateListOf<Language>() }
    val selectedSubjects = remember { mutableStateListOf<TutoringSubject>() }
    val expandedSubjectDropdown = remember { mutableStateOf(false) }
    val sliderValue = remember { mutableFloatStateOf(5f) }
    val showError = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Text(
                text = "Welcome Tutor!",
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp).testTag("welcomeText"),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(paddingValues)
                    .testTag("tutorInfoScreen"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Complete your profile by selecting your capabilities:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("instructionText")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("What languages do you feel comfortable teaching in?", style = MaterialTheme.typography.titleSmall)
                // Language Selection
                LanguageSelection(selectedLanguages)

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Selection
                Text("Which subjects do you teach?", style = MaterialTheme.typography.titleSmall)
                SubjectDropdown(selectedSubjects, expandedSubjectDropdown)

                Spacer(modifier = Modifier.height(16.dp))

                // Price Selection
                Text("Select your tutoring price per hour:", style = MaterialTheme.typography.titleSmall)
                PriceSlider(sliderValue)
            }
        },
        bottomBar = {
            // Confirmation Button with Validation
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("confirmButton"),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    if (selectedLanguages.isEmpty() || selectedSubjects.isEmpty()) {
                        showError.value = true
                        Toast.makeText(context, "Please select at least one language and one subject", Toast.LENGTH_SHORT).show()
                    } else {
                        showError.value = false
                        profile.languages.clear()
                        profile.languages.addAll(selectedLanguages)
                        profile.subjects.clear()
                        profile.subjects.addAll(selectedSubjects)
                        profile.price = sliderValue.floatValue.toInt()

                        listProfilesViewModel.updateProfile(profile)
                        navigationActions.navigateTo(Screen.CREATE_TUTOR_SCHEDULE)

                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Continue")
            }
        }
    )
}

@Composable
fun LanguageSelection(selectedLanguages: MutableList<Language>) {
    val languages = Language.entries.toTypedArray()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        languages.forEach { language ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Checkbox(
                    checked = selectedLanguages.contains(language),
                    onCheckedChange = { isSelected ->
                        if (isSelected) {
                            selectedLanguages.add(language)
                        } else {
                            selectedLanguages.remove(language)
                        }
                    },
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = language.name.lowercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}




@Composable
fun SubjectDropdown(selectedSubjects: MutableList<TutoringSubject>, expandedSubjectDropdown: MutableState<Boolean>) {
    val subjects = TutoringSubject.entries.toTypedArray()
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expandedSubjectDropdown.value = true }, modifier = Modifier.fillMaxWidth().testTag("subjectButton")) {
            Text("Select Subjects")
        }
        DropdownMenu(
            expanded = expandedSubjectDropdown.value,
            onDismissRequest = { expandedSubjectDropdown.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            subjects.forEach { subject ->
                val isSelected = selectedSubjects.contains(subject)
                DropdownMenuItem(
                    text = {
                        Row {
                            if (isSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                            Text(subject.name.lowercase())
                        }
                    },
                    onClick = {
                        if (isSelected) {
                            selectedSubjects.remove(subject)
                        } else {
                            selectedSubjects.add(subject)
                        }
                    },
                    modifier = Modifier.testTag("dropdown${subject.name}")
                )
            }
        }
    }
}

@Composable
fun PriceSlider(sliderValue: MutableFloatState) {
    val averagePrice = 30
    Slider(
        value = sliderValue.floatValue,
        onValueChange = { sliderValue.floatValue = it },
        valueRange = 5f..50f,
        steps = 45,
        modifier = Modifier.padding(horizontal = 16.dp).testTag("priceSlider")
    )

    val priceDifference = averagePrice - sliderValue.floatValue.toInt()
    if (priceDifference >= 0) {
        Text(
            "Your price is ${sliderValue.floatValue.toInt()}.-, which is $priceDifference.- less than the average.",
            modifier = Modifier.testTag("priceDifferenceLow")
        )
    } else {
        Text(
            "Your price is ${sliderValue.floatValue.toInt()}.-, which is ${-priceDifference}.- more than the average.",
            modifier = Modifier.testTag("priceDifferenceHigh")
        )
    }
}