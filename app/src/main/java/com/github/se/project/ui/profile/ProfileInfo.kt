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
import com.github.se.project.utils.capitalizeFirstLetter

/**
 * A composable function to display a user's profile information screen.
 *
 * @param navigationActions The actions to handle navigation between screens.
 * @param listProfilesViewModel The ViewModel that manages user profile data.
 * @param lessonViewModel The ViewModel that manages lesson data for the user.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
  // Observes the current user's profile state.
  val profileState = listProfilesViewModel.currentProfile.collectAsState()
  // Observes the list of lessons for the current user.
  val lessons = lessonViewModel.currentUserLessons.collectAsState()

  // Defines the section for completed lessons with an icon and status.
  val completedLessonsSection =
      SectionInfo(
          title = "Completed Lessons",
          status = LessonStatus.COMPLETED,
          icon = Icons.Default.CheckCircle)

  // Filters the list of lessons to get completed or pending review lessons.
  val completedLessons =
      lessons.value.filter {
        it.status == LessonStatus.COMPLETED || it.status == LessonStatus.PENDING_REVIEW
      }

  // Sets up the Scaffold layout for the screen with a top bar and content area.
  Scaffold(
      topBar = {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .testTag("profileTopBar"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                // Display title if the user is a tutor.
                if (profileState.value?.role == Role.TUTOR) {
                  Text(
                      text = stringResource(id = R.string.your_account),
                      style = MaterialTheme.typography.titleLarge,
                      color = MaterialTheme.colorScheme.onBackground,
                      modifier = Modifier.padding(end = 16.dp) // Adds space between title and icons
                      )
                }
                // Edit profile button to navigate to the profile edit screen.
                IconButton(
                    onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
                    modifier = Modifier.testTag("editProfileButton")) {
                      Icon(
                          imageVector = Icons.Default.Edit,
                          contentDescription = stringResource(id = R.string.edit_profile),
                          tint = MaterialTheme.colorScheme.onBackground)
                    }
              }

              // Close button to navigate back to the previous screen.
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

        // Ensures user profile data is loaded.
        profileState.value?.let { userProfile ->

          // If the user is a tutor, fetch lessons for the tutor. Otherwise, fetch lessons for a
          // student.
          val isTutor = userProfile.role == Role.TUTOR
          if (isTutor) {
            lessonViewModel.getLessonsForTutor(userProfile.uid)
          } else {
            lessonViewModel.getLessonsForStudent(userProfile.uid)
          }

          // Profile content layout, with all information wrapped in a card.
          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .verticalScroll(rememberScrollState())) {

                // Card displaying profile information.
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

                            // Row displaying the user's name and profile icon.
                            Row(
                                modifier = Modifier.fillMaxWidth().testTag("profileInfoRow"),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {

                                  // Icon representing the user's profile image.
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

                                  // User name and academic information.
                                  Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text =
                                            "${userProfile.firstName.capitalizeFirstLetter()} ${userProfile.lastName.capitalizeFirstLetter()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.testTag("profileName"),
                                        color = MaterialTheme.colorScheme.onBackground)

                                    // Display the user's section and academic level.
                                    Text(
                                        text =
                                            "${userProfile.section} - ${userProfile.academicLevel}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.testTag("profileAcademicInfo"))
                                  }
                                }

                            // Divider separating profile name and other information.
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Row displaying the user's role.
                            Row(
                                modifier = Modifier.fillMaxWidth().testTag("roleRow"),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  Icon(
                                      imageVector = Icons.Default.Person,
                                      contentDescription = "Role",
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  // Role text displaying the user's role.
                                  Text(
                                      text = "Role: ${userProfile.role.name}",
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onBackground)
                                }

                            // Row displaying the languages known by the user.
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                  Icon(
                                      imageVector =
                                          ImageVector.vectorResource(
                                              id = R.drawable.baseline_language_24),
                                      contentDescription = "Languages",
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  // Column displaying the list of languages.
                                  Column(horizontalAlignment = Alignment.Start) {
                                    userProfile.languages.forEach { language ->
                                      Text(
                                          text = language.name,
                                          color = MaterialTheme.colorScheme.onBackground,
                                          modifier = Modifier.testTag(language.name))
                                    }
                                  }
                                }

                            // Row displaying the user's phone number.
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                  Icon(
                                      imageVector = Icons.Default.Call,
                                      contentDescription =
                                          stringResource(id = R.string.phone_number),
                                      tint = MaterialTheme.colorScheme.primary,
                                      modifier = Modifier.size(24.dp))

                                  Text(
                                      text = userProfile.phoneNumber,
                                      style = MaterialTheme.typography.bodyMedium,
                                      color = MaterialTheme.colorScheme.onBackground,
                                      modifier = Modifier.testTag("phoneNumberRow"))
                                }

                            // If the user is a tutor, display the price per lesson.
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

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                            // Lessons Count
                            Text(
                                text =
                                    "${completedLessons.size} ${if (isTutor) stringResource(id = R.string.lessons_given) else stringResource(id = R.string.lessons_taken)}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.testTag("lessonsCount"),
                                color = MaterialTheme.colorScheme.onBackground)

                            // Display Completed Lessons Section
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
        }
            ?: run {
              Text(
                  text = stringResource(id = R.string.error_loading_profile),
                  color = MaterialTheme.colorScheme.error,
                  modifier = Modifier.testTag("errorLoadingProfile"))
            }
      }
}
