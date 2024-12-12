@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.project.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayTutorDetails
import com.github.se.project.ui.components.ErrorState
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

@Composable
fun FavoriteTutorDetailsScreen(
    listProfilesViewModel: ListProfilesViewModel,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions
) {
  val tutorProfile =
      listProfilesViewModel.selectedProfile.collectAsState().value
          ?: return Text(stringResource(R.string.no_profile_selected))

  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(stringResource(R.string.no_profile_selected))

  val completedLessons by lessonViewModel.currentUserLessons.collectAsState()
  val ratedLessons =
      completedLessons.filter {
        it.tutorUid.contains(tutorProfile.uid) &&
            it.status == LessonStatus.COMPLETED &&
            it.rating != null
      }

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
                  text = stringResource(R.string.favorite_tutors),
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("favoriteTutorDetailsTitle"))
            },
            actions = {
              IconButton(
                  onClick = {
                    val newProfile =
                        currentProfile.copy(
                            favoriteTutors =
                                if (currentProfile.favoriteTutors.contains(tutorProfile.uid))
                                    currentProfile.favoriteTutors - tutorProfile.uid
                                else currentProfile.favoriteTutors + tutorProfile.uid)

                    listProfilesViewModel.updateProfile(newProfile)
                    listProfilesViewModel.setCurrentProfile(newProfile)
                  },
                  modifier = Modifier.testTag("bookmarkButton")) {
                    Icon(
                        imageVector =
                            if (currentProfile.favoriteTutors.contains(tutorProfile.uid))
                                Icons.Default.Star
                            else Icons.Outlined.Star,
                        contentDescription = "Bookmark Tutor",
                        tint =
                            if (currentProfile.favoriteTutors.contains(tutorProfile.uid))
                                MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                  }
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("favoriteTutorDetailsScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {

              // Check if the given profile is a tutor
              if (tutorProfile.role != Role.TUTOR) {
                ErrorState(message = stringResource(R.string.no_tutor_selected))
              } else {
                // Tutor information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                      // Tutor information
                      DisplayTutorDetails(
                          tutorProfile = tutorProfile,
                          completedLessons = ratedLessons,
                          listProfilesViewModel = listProfilesViewModel)
                    }

                Spacer(modifier = Modifier.weight(1f))

                // Ask for a lesson with this tutor
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                      listProfilesViewModel.selectProfile(tutorProfile)
                      lessonViewModel.unselectLesson()
                      navigationActions.navigateTo(Screen.ADD_LESSON_WITH_FAVORITE)
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("askLessonButton")) {
                      Text(stringResource(R.string.ask_lesson_with_tutor))
                    }
              }
            }
      }
}
