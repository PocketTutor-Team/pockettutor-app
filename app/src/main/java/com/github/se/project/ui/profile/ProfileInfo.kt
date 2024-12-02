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
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel
) {
  // State to collect current profile data
  val profileState = listProfilesViewModel.currentProfile.collectAsState()
  // State to collect current lessons data
  val lessons = lessonViewModel.currentUserLessons.collectAsState()

  // SectionInfo for displaying completed lessons
  val completedLessonsSection =
      SectionInfo(
          title = "Completed Lessons", // Section title
          status = LessonStatus.COMPLETED, // Filter for completed lessons
          icon = Icons.Default.CheckCircle // Icon for the section
          )

  // Filtering the completed lessons
  val completedLessons =
      lessons.value.filter {
        it.status == LessonStatus.COMPLETED || it.status == LessonStatus.PENDING_REVIEW
      }

  Scaffold(
      topBar = {
        // Top bar layout with title and profile edit buttons
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .testTag("profileTopBar"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                // Display profile title if user is a tutor
                if (profileState.value?.role == Role.TUTOR) {
                  Text(
                      text = stringResource(id = R.string.your_account), // Account label
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.onBackground,
                      modifier = Modifier.padding(end = 16.dp))
                }
                // Edit profile button
                IconButton(
                    onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
                    modifier = Modifier.testTag("editProfileButton")) {
                      Icon(
                          imageVector = Icons.Default.Edit,
                          contentDescription = stringResource(id = R.string.edit_profile),
                          tint = MaterialTheme.colorScheme.onBackground)
                    }
              }

              // Close button in top bar
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("closeButton")) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }
            }
      }) { paddingValues ->
        profileState.value?.let { userProfile ->
          // Get lessons for tutor or student based on role
          val isTutor = userProfile.role == Role.TUTOR
          if (isTutor) {
            lessonViewModel.getLessonsForTutor(userProfile.uid)
          } else {
            lessonViewModel.getLessonsForStudent(userProfile.uid)
          }

          // Main profile layout wrapped in a scrollable column
          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .verticalScroll(rememberScrollState())) {
                // Profile info card container
                Card(
                    modifier =
                        Modifier.fillMaxWidth().padding(vertical = 2.dp).testTag("profileInfoCard"),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceContainerLowest.copy(
                                    alpha = 0.5f))) {
                      Column(
                          modifier = Modifier.padding(16.dp),
                          verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Profile Name and Academic Info Section
                            Row(
                                modifier = Modifier.fillMaxWidth().testTag("profileInfoRow"),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  // Profile Icon (Avatar)
                                  Surface(
                                      modifier = Modifier.size(48.dp),
                                      shape = MaterialTheme.shapes.medium,
                                      color =
                                          MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.padding(8.dp),
                                            tint = MaterialTheme.colorScheme.primary)
                                      }

                                  // Profile Name and Academic Info
                                  Column(modifier = Modifier.weight(1f)) {
                                    // Display profile name
                                    Text(
                                        text =
                                            "${userProfile.firstName.capitalizeFirstLetter()} ${userProfile.lastName.capitalizeFirstLetter()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.testTag("profileName"),
                                        color = MaterialTheme.colorScheme.onBackground)

                                    // Display academic level and section
                                    Text(
                                        text =
                                            "${userProfile.section} - ${userProfile.academicLevel}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.testTag("profileAcademicInfo"))
                                  }
                                }

                            // Divider separating profile name and rest of the information
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Role Display Row
                            Row(
                                modifier = Modifier.fillMaxWidth().testTag("roleRow"),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  // Role Icon
                                  Icon(
                                      imageVector = Icons.Default.Person,
                                      contentDescription = "Role",
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  // Role Text
                                  Text(
                                      text = "Role: ${userProfile.role.name}",
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onBackground)
                                }

                            // Language Display Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                  // Language Icon
                                  Icon(
                                      imageVector =
                                          ImageVector.vectorResource(
                                              id = R.drawable.baseline_language_24),
                                      contentDescription = "Languages",
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  // Languages Column
                                  Column(horizontalAlignment = Alignment.Start) {
                                    userProfile.languages.forEach { language ->
                                      Text(
                                          text = language.name,
                                          color = MaterialTheme.colorScheme.onBackground,
                                          modifier = Modifier.testTag(language.name))
                                    }
                                  }
                                }

                            // Phone Number Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  // Phone Icon
                                  Icon(
                                      imageVector = Icons.Default.Call,
                                      contentDescription =
                                          stringResource(id = R.string.phone_number),
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  // Phone Number Text
                                  Text(
                                      text = userProfile.phoneNumber,
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onBackground,
                                      modifier = Modifier.testTag("phoneNumberRow"))
                                }

                            // Price Section (only for Tutor)
                            if (isTutor) {
                              Row(
                                  modifier = Modifier.fillMaxWidth().testTag("priceRow"),
                                  horizontalArrangement = Arrangement.spacedBy(16.dp),
                                  verticalAlignment = Alignment.CenterVertically) {
                                    // Price Icon
                                    Icon(
                                        imageVector =
                                            ImageVector.vectorResource(
                                                id = R.drawable.baseline_access_time_24),
                                        contentDescription =
                                            stringResource(id = R.string.price_per_lesson),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp))

                                    // Price Text
                                    Text(
                                        text =
                                            "${stringResource(id = R.string.price_per_lesson)} ${userProfile.price}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.testTag("priceText"))
                                  }
                            }
                          }
                    }

                // Lessons Section (only if there are completed lessons)
                if (completedLessons.isNotEmpty()) {
                  ExpandableLessonSection(
                      section = completedLessonsSection,
                      lessons = completedLessons,
                      isTutor = isTutor,
                      onClick = { lesson ->
                        lessonViewModel.selectLesson(lesson)
                        navigationActions.navigateTo(Screen.CONFIRMED_LESSON)
                      },
                      listProfilesViewModel = listProfilesViewModel)
                }

                // Log Out Button at the bottom
                Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom
                Button(
                    onClick = {
                      // Set the current profile to null
                      listProfilesViewModel.setCurrentProfile(null)
                      // Navigate to SignIn screen
                      navigationActions.navigateTo(Screen.AUTH)
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .testTag("signOutButton")) {
                      Text(text = "Log Out")
                    }
              }
        }
            ?: run {
              // Error message if profile loading fails
              Text(
                  text = stringResource(id = R.string.error_loading_profile),
                  color = MaterialTheme.colorScheme.error,
                  modifier = Modifier.testTag("errorLoadingProfile"))
            }
      }
}
