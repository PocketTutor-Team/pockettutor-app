package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessons
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun ProfileInfoScreen(
    navigationActions: NavigationActions,
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory)
) {
  val profileState = listProfilesViewModel.currentProfile.collectAsState()
  val lessons = lessonViewModel.currentUserLessons.collectAsState()

  // Filter only lessons that are marked as COMPLETED
  val completedLessons = lessons.value.filter { it.status == LessonStatus.COMPLETED }

  Scaffold(
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp).testTag("profileTopBar"),
            horizontalArrangement = Arrangement.SpaceBetween) {
              Row(Modifier.padding(horizontal = 4.dp)) {
                if (profileState.value?.role == Role.TUTOR) {
                  IconButton(
                      onClick = { navigationActions.navigateTo(Screen.EDIT_SCHEDULE) },
                      modifier = Modifier.testTag("calendarButton")) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar")
                      }
                }
                IconButton(
                    onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) },
                    modifier = Modifier.testTag("editProfileButton")) {
                      Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
              }

              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("closeButton")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                  }
            }
      }) { paddingValues ->
        profileState.value?.let { userProfile ->
          val isTutor = userProfile.role == Role.TUTOR

          if (isTutor) {
            lessonViewModel.getLessonsForTutor(userProfile.uid)
          } else {
            lessonViewModel.getLessonsForStudent(userProfile.uid)
          }

          Column(
              modifier =
                  Modifier.padding(paddingValues)
                      .padding(horizontal = 32.dp, vertical = 8.dp)
                      .testTag("profileContent")) {
                Text(
                    text = "${userProfile.firstName} ${userProfile.lastName}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag("profileName"))
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDetails(userProfile)
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text =
                        "${completedLessons.size} ${if (isTutor) "lessons given" else "lessons taken"} since you joined PocketTutor",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("lessonsCount"))
                DisplayLessons(lessons = completedLessons, isTutor = isTutor)
              }
        }
            ?: run {
              // Display message if profile is null
              Text(
                  text = "Error loading profile...",
                  modifier = Modifier.testTag("errorLoadingProfile"))
            }
      }
}

@Composable
fun ProfileDetails(profile: Profile) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(4.dp).testTag("profileDetails"),
      verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text =
                "Status: ${profile.academicLevel} ${if (profile.role == Role.TUTOR) "Tutor" else "Student"}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("profileStatus"))
        Text(
            text = "Section: ${profile.section}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("profileSection"))
        // Display role-specific information
        if (profile.role == Role.TUTOR) {
          Text(
              text = "Price: ${profile.price}.- per hour",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("profilePrice"))
        }
      }
}
