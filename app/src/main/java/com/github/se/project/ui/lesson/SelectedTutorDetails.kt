package com.github.se.project.ui.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.ErrorState
import com.github.se.project.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedTutorDetailsScreen(
    tutorProfile: Profile,
    onChoiceConfirmation: (Profile) -> Unit,
    navigationActions: NavigationActions
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag("topBar"),
                navigationIcon = {
                    IconButton(
                        onClick = { navigationActions.goBack() },
                        modifier = Modifier.testTag("backButton")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        text = "Available Tutors",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("confirmLessonTitle"))

                })
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("selectedTutorDetailsScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // Check if the given profile is a tutor
            if (tutorProfile.role != Role.TUTOR) {
                ErrorState(message = "No tutor selected. Should not happen.")

            } else {
                // Tutor information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    DisplayTutorDetails(tutorProfile)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Confirmation button
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("confirmButton")) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Confirmation Button Icon",
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm your lesson with ${tutorProfile.firstName} ${tutorProfile.lastName}")
                }
            }
        }

        // Confirmation Dialog
        if (showConfirmDialog) {
            AlertDialog(
                modifier = Modifier.testTag("confirmDialog"),
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Text(
                        text = "Confirm Your Choice", modifier = Modifier.testTag("confirmDialogTitle"))
                },
                text = {
                    Text(
                        text =
                        "Would you like to choose this tutor for your lesson and pay a price of ${tutorProfile.price}.-/hour?",
                        modifier = Modifier.testTag("confirmDialogText"))
                },
                confirmButton = {
                    Button(
                        modifier = Modifier.testTag("confirmDialogButton"),
                        onClick = {
                            onChoiceConfirmation(tutorProfile)
                            showConfirmDialog = false
                        }) {
                        Modifier.testTag("confirmButton")
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                        modifier = Modifier.testTag("confirmDialogCancelButton"),
                        onClick = { showConfirmDialog = false }) {
                        Text("Cancel")
                    }
                })
        }
    }
}


// Display the tutor details
@Composable
private fun DisplayTutorDetails(
    tutorProfile: Profile
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp).testTag("tutorDetailsCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tutor information section
            TutorInfoSection(tutorProfile)

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            // Lesson information section
            TutorDescriptionSection(tutorProfile.description)
        }
    }
}

@Composable
private fun TutorInfoSection(profile: Profile) {
    Row(
        modifier = Modifier.fillMaxWidth().testTag("tutorInfoRow"),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture placeholder
        Surface(
            modifier = Modifier.size(48.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.primary)
        }

        // Tutor details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${profile.firstName} ${profile.lastName}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("tutorName"))
            Text(
                text = "${profile.section} - ${profile.academicLevel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("tutorAcademicInfo"))
        }

        // Price
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${profile.price}.-/h",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TutorDescriptionSection(description: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("tutorDescriptionSection")
    ) {
        if (description.isEmpty()) {
            Text(
                text = "No description available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("tutorDescriptionEmpty"))
        } else {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("tutorDescription"))
        }
    }
}