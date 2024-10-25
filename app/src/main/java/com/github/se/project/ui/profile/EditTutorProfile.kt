package com.github.se.project.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.github.se.project.ui.navigation.NavigationActions

@Composable
fun EditTutorProfile(
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

    val initialLanguagesList: List<Language> = profile.languages.toList()
    val selectedLanguages: SnapshotStateList<Language> = remember { mutableStateListOf(*initialLanguagesList.toTypedArray()) }

    val initialSubjectsList: List<Subject> = profile.subjects.toList()
    val selectedSubjects: SnapshotStateList<Subject> = remember { mutableStateListOf(*initialSubjectsList.toTypedArray()) }

    val sliderValue = remember { mutableFloatStateOf(profile.price.toFloat()) }
    val showError = remember { mutableStateOf(false) }

    val academicLevel = remember { mutableStateOf(profile.academicLevel.name) }
    val section = remember { mutableStateOf(profile.section.name) }
    var expandedSection by remember { mutableStateOf(false) }
    var expandedAcademicLevel by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            IconButton(onClick = { navigationActions.goBack() },
                modifier = Modifier.testTag("closeButton")) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go Back") }
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
                    text = "${profile.firstName} ${profile.lastName}",
                    modifier = Modifier,//.padding(vertical = 48.dp, horizontal = 16.dp).testTag("welcomeText"),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center)
                Text(
                    "Modify your profile information:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("instructionText"))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Teaching languages:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("languageText"))
                // Language Selection
                LanguageSelector(selectedLanguages)

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Selection
                Text(
                    "Teaching subjects:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("subjectText"))
                SubjectsSelector(selectedSubjects)

                Spacer(modifier = Modifier.height(16.dp))

                // Price Selection
                Text(
                    "Tutoring price per hour:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("priceText"))
                PriceSlider(sliderValue)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Modify your section", style = MaterialTheme.typography.titleSmall)
                // Section dropdown menu with improved styling
                Box {
                    Text(
                        text = section.value, // Directly access the value
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSection = true }
                            .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("sectionDropdown"),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    DropdownMenu(
                        expanded = expandedSection,
                        onDismissRequest = { expandedSection = false },
                        modifier = Modifier.fillMaxWidth().zIndex(1f),
                        properties = PopupProperties(focusable = true)
                    ) {
                        Section.entries.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    section.value = s.name
                                    expandedSection = false
                                },
                                modifier = Modifier.testTag("sectionDropdownItem-${s.name}")
                            )
                        }
                    }
                }

                Text(text = "Modify your academic level", style = MaterialTheme.typography.titleSmall)
                Box {
                    Text(
                        text = academicLevel.value, // Directly access the value
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedAcademicLevel = true }
                            .background(Color.Transparent, shape = MaterialTheme.shapes.small)
                            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                            .padding(16.dp)
                            .testTag("academicLevelDropdown"),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    DropdownMenu(
                        expanded = expandedAcademicLevel,
                        onDismissRequest = { expandedAcademicLevel = false },
                        modifier = Modifier.fillMaxWidth().zIndex(1f),
                        properties = PopupProperties(focusable = true)
                    ) {
                        AcademicLevel.entries.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.name, style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    academicLevel.value = a.name
                                    expandedAcademicLevel = false
                                },
                                modifier = Modifier.testTag("academicLevelDropdownItem-${a.name}")
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Confirmation Button with Validation
            Button(
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("confirmButton"),
                shape = MaterialTheme.shapes.medium,
                onClick = {
                    if (selectedLanguages.isEmpty() || selectedSubjects.isEmpty()) {
                        showError.value = true
                        Toast.makeText(
                            context,
                            "Please select at least one language and one subject",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        showError.value = false
                        profile.languages.clear()
                        profile.languages.addAll(selectedLanguages)
                        profile.subjects.clear()
                        profile.subjects.addAll(selectedSubjects)
                        profile.price = sliderValue.floatValue.toInt()
                        profile.academicLevel = AcademicLevel.valueOf(academicLevel.value) // Adjust based on your enum definition
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