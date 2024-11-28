package com.github.se.project.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.ExpandableLessonSection
import com.github.se.project.ui.components.SectionInfo
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
    val profileState = listProfilesViewModel.currentProfile.collectAsState()
    val lessons = lessonViewModel.currentUserLessons.collectAsState()

    // SectionInfo for Completed Lessons
    val completedLessonsSection = SectionInfo(
        title = "Completed Lessons",
        status = LessonStatus.COMPLETED,
        icon = Icons.Default.CheckCircle
    )

    // Filter completed lessons
    val completedLessons =
        lessons.value.filter {
            it.status == LessonStatus.COMPLETED || it.status == LessonStatus.PENDING_REVIEW
        }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .testTag("profileTopBar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (profileState.value?.role == Role.TUTOR) {
                        // Title
                        Text(
                            text = "Your Account",
                            style = MaterialTheme.typography.titleLarge, // Adjust the style as needed
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(end = 16.dp) // Spacing between title and icons
                        )
                    }
                    IconButton(
                        onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
                        modifier = Modifier.testTag("editProfileButton")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                IconButton(
                    onClick = { navigationActions.goBack() },
                    modifier = Modifier.testTag("closeButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

    ) { paddingValues ->
        profileState.value?.let { userProfile ->
            val isTutor = userProfile.role == Role.TUTOR
            if (isTutor) {
                lessonViewModel.getLessonsForTutor(userProfile.uid)
            } else {
                lessonViewModel.getLessonsForStudent(userProfile.uid)
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Entire profile content is wrapped in a single Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .testTag("profileInfoCard"),
                    colors = CardDefaults.cardColors(
                        //containerColor = MaterialTheme.colorScheme.surfaceVariant
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profileInfoRow"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile Icon Surface
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Profile Name and Academic Info in a Column
                            Column(modifier = Modifier.weight(1f)) {
                                // Profile Name
                                Text(
                                    text = "${userProfile.firstName} ${userProfile.lastName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.testTag("profileName"),
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                // Section and Academic Level
                                Text(
                                    text = "${userProfile.section} - ${userProfile.academicLevel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag("profileAcademicInfo")
                                )
                            }
                        }

                        //draw a line between the profile name and the rest of the profile info
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Role Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("roleRow"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Role Icon
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Role",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )

                            // Role Text
                            Text(
                                text = "Role: ${userProfile.role.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("languagesRow"),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Icon representing languages
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_language_24),
                                contentDescription = "Languages",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )

                            // Column for languages list displayed vertically
                            Column(horizontalAlignment = Alignment.Start) {
                                userProfile.languages.forEach { language ->
                                    Text(
                                        text = language.name,
                                        //style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        // Phone Number Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phoneNumberRow"),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Phone Icon
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Phone Number",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )

                            // Phone Number Text
                            Text(
                                text = userProfile.phoneNumber ?: "N/A", // Handle if phone number is null
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Price Row (for Tutor)
                        if (isTutor) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("priceRow"),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Price Icon
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_access_time_24),
                                    contentDescription = "Price per Lesson",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )

                                // Price Text
                                Text(
                                    text = "Price: ${userProfile.price ?: "Not Set"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }



                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        // Lessons Count
                        Text(
                            text = "${completedLessons.size} ${if (isTutor) " lessons given" else "lessons taken"} since you joined PocketTutor",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.testTag("lessonsCount"),
                            color = MaterialTheme.colorScheme.onBackground
                        )


                        // Display Completed Lessons Section
                        ExpandableLessonSection(
                            section = completedLessonsSection,
                            lessons = completedLessons,
                            isTutor = isTutor,
                            onClick = { lesson ->
                                lessonViewModel.selectLesson(lesson)
                                navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
                            },
                            listProfilesViewModel = listProfilesViewModel
                        )
                    }
                }
            }
        } ?: run {
            Text(
                text = "Error loading profile...",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

