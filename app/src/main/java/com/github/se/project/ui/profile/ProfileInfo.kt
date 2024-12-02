package com.github.se.project.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.ExpandableLessonSection
import com.github.se.project.ui.components.SectionInfo
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.capitalizeFirstLetter

@SuppressLint("SuspiciousIndentation")
/**
 * A composable function that represents the profile information screen.
 *
 * @param navigationActions Handles navigation between different screens.
 * @param listProfilesViewModel ViewModel to fetch and observe user profiles.
 * @param lessonViewModel ViewModel to fetch and manage lesson-related data for the user.
 */
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions, // Navigation actions for transitioning between screens
    listProfilesViewModel: ListProfilesViewModel, // ViewModel for managing profile data
    lessonViewModel: LessonViewModel // ViewModel for managing lessons
) {
    // Observes the current user's profile data
    val profileState = listProfilesViewModel.currentProfile.collectAsState()

    // Observes the current user's lessons data
    val lessons = lessonViewModel.currentUserLessons.collectAsState()

    // Section information for completed lessons, defines the title, status, and icon
    val completedLessonsSection =
        SectionInfo(
            title = "Completed Lessons", // Title for completed lessons section
            status = LessonStatus.COMPLETED, // Status filter to display completed lessons
            icon = Icons.Default.CheckCircle) // Icon to represent completed lessons section

    // Filters completed lessons or lessons that are pending review
    val completedLessons =
        lessons.value.filter {
            it.status == LessonStatus.COMPLETED || it.status == LessonStatus.PENDING_REVIEW
        }

    Scaffold(
        topBar = {
            // Top bar layout for profile screen with title and icons
            Row(
                modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background) // Background color of top bar
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .testTag("profileTopBar"), // Test tag for top bar
                horizontalArrangement = Arrangement.SpaceBetween, // Horizontal arrangement of title and icons
                verticalAlignment = Alignment.CenterVertically) {
                // Profile title and edit button in a row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Show the account title if the user is a tutor
                    if (profileState.value?.role == Role.TUTOR) {
                        Text(
                            text = stringResource(id = R.string.your_account), // Text for the title
                            style = MaterialTheme.typography.titleLarge, // Text style for large titles
                            color = MaterialTheme.colorScheme.onBackground, // Text color
                            modifier = Modifier.padding(end = 16.dp)) // Padding to the right of the title
                    }
                    // Edit profile button
                    IconButton(
                        onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) }, // Navigate to edit profile screen
                        modifier = Modifier.testTag("editProfileButton")) {
                        Icon(
                            imageVector = Icons.Default.Edit, // Edit icon
                            contentDescription = stringResource(id = R.string.edit_profile), // Description for accessibility
                            tint = MaterialTheme.colorScheme.onBackground) // Icon color
                    }
                }

                // Close button to navigate back to the previous screen
                IconButton(
                    onClick = { navigationActions.goBack() }, // Go back to the previous screen
                    modifier = Modifier.testTag("closeButton")) {
                    Icon(
                        imageVector = Icons.Default.Close, // Close icon
                        contentDescription = stringResource(id = R.string.close), // Close button description
                        tint = MaterialTheme.colorScheme.onBackground) // Icon color
                }
            }
        }) { paddingValues ->
        // Once the profile data is loaded, display the profile information
        profileState.value?.let { userProfile ->
            // Fetch lessons for the tutor or student based on the role
            if (userProfile.role == Role.TUTOR) {
                lessonViewModel.getLessonsForTutor(userProfile.uid)
            } else {
                lessonViewModel.getLessonsForStudent(userProfile.uid)
            }

            Column(
                modifier =
                Modifier.padding(paddingValues) // Padding for the main content
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())) { // Allow scrolling for long profile content
                // Profile Card containing all profile information
                Card(
                    modifier =
                    Modifier.fillMaxWidth().padding(vertical = 2.dp).testTag("profileInfoCard"), // Card layout
                    colors =
                    CardDefaults.cardColors(
                        containerColor =
                        MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f))) { // Card color
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Profile Name and Section Info
                        Row(
                            modifier = Modifier.fillMaxWidth().testTag("profileInfoRow"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between name and icon
                            verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(48.dp), // Icon size
                                shape = MaterialTheme.shapes.medium, // Rounded icon shape
                                color =
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) { // Background color
                                Icon(
                                    imageVector = Icons.Default.Person, // Profile icon
                                    contentDescription = null, // No content description
                                    modifier = Modifier.padding(8.dp), // Padding around the icon
                                    tint = MaterialTheme.colorScheme.primary) // Icon color
                            }

                            // Profile Name and Academic Information in a Column
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text =
                                    "${userProfile.firstName.capitalizeFirstLetter()} ${userProfile.lastName.capitalizeFirstLetter()}", // Full name
                                    style = MaterialTheme.typography.titleMedium, // Title style for name
                                    modifier = Modifier.testTag("profileName"), // Test tag for name
                                    color = MaterialTheme.colorScheme.onBackground) // Text color

                                Text(
                                    text =
                                    "${userProfile.section} - ${userProfile.academicLevel}", // Academic details
                                    style = MaterialTheme.typography.bodyMedium, // Body text style
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Text color
                                    modifier = Modifier.testTag("profileAcademicInfo")) // Test tag for academic info
                            }
                        }

                        // Divider between profile name and other info
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Role row, shows user role (Tutor/Student)
                        Row(
                            modifier = Modifier.fillMaxWidth().testTag("roleRow"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between icon and text
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person, // Role icon
                                contentDescription = "Role", // Accessibility description
                                tint = MaterialTheme.colorScheme.primary, // Icon color
                                modifier = Modifier.size(24.dp)) // Icon size

                            Text(
                                text = "Role: ${userProfile.role.name}", // Displays user role
                                style = MaterialTheme.typography.bodyMedium, // Text style
                                color = MaterialTheme.colorScheme.onBackground) // Text color
                        }

                        // Languages spoken by the user
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(
                                imageVector =
                                ImageVector.vectorResource(
                                    id = R.drawable.baseline_language_24), // Language icon
                                contentDescription = "Languages", // Description
                                tint = MaterialTheme.colorScheme.primary, // Icon color
                                modifier = Modifier.size(24.dp)) // Icon size

                            // List of languages spoken
                            Column(horizontalAlignment = Alignment.Start) {
                                userProfile.languages.forEach { language ->
                                    Text(
                                        text = language.name, // Language name
                                        color = MaterialTheme.colorScheme.onBackground, // Text color
                                        style = MaterialTheme.typography.bodySmall) // Text style
                                }
                            }
                        }

                        // Contact info section with button
                        SectionInfo(
                            title = "Contact Information",
                            status = LessonStatus.PENDING_REVIEW,
                            icon = Icons.Default.Call)

                        // Button for contacting the user
                        Button(
                            onClick = { /* Handle call action */ },
                            modifier = Modifier.fillMaxWidth().testTag("contactButton")) {
                            Text(text = "Call User") // Button label
                        }
                    }
                }
            }
        }
    }
}
