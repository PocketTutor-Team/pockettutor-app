package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayTutors
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorMatchingScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text("No profile selected. Should not happen.")

  val currentLesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen.")

  val tutorProfilesFlow = remember {
    listProfilesViewModel.profiles.filter { profiles: List<Profile> ->
      profiles.any { profile -> profile.role == Role.TUTOR }
    }
  }
  val allTutorProfiles by tutorProfilesFlow.collectAsState(listOf())

  val filteredTutor =
      if (currentLesson.status == LessonStatus.MATCHING) {
        allTutorProfiles.filter { profile -> // TODO: think of the filtering
          profile.subjects.contains(currentLesson.subject) &&
              profile.price <= currentLesson.maxPrice &&
              profile.price >= currentLesson.minPrice &&
              isTutorAvailable(profile.schedule, currentLesson.timeSlot)
        }
      } else {
        val tutorList =
            allTutorProfiles.filter { profile -> currentLesson.tutorUid.contains(profile.uid) }
        tutorList.ifEmpty {
          return Text("No tutor for the selected lesson. Should not happen.")
        }
      }

  val context = LocalContext.current

  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("topAppBar"),
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("backButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back arrow")
                  }
            },
            title = {
              Text(
                  text = "Available Tutors",
                  style = MaterialTheme.typography.titleLarge,
                  modifier = Modifier.testTag("AvailableTutorsTitle"))
            },
            actions = {
              IconButton(
                  onClick = { /* TODO: Additional filter options */},
                  modifier = Modifier.testTag("filterButton")) {
                    Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Filter")
                  }
            })
      },
      bottomBar = {
        if (currentLesson.status == LessonStatus.MATCHING) {
          Button(
              modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("noTutorButton"),
              onClick = {
                lessonViewModel.addLesson(
                    currentLesson.copy(status = LessonStatus.STUDENT_REQUESTED),
                    onComplete = {
                      lessonViewModel.getLessonsForStudent(currentProfile.uid)
                      Toast.makeText(context, "Lesson sent successfully!", Toast.LENGTH_SHORT)
                          .show()
                    })
                navigationActions.navigateTo(Screen.HOME)
              }) {
                Text(
                    "Ask other tutor for your lesson",
                    modifier = Modifier.testTag("noTutorButtonText"))
              }
        }
      }) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
          if (filteredTutor.isEmpty()) {
            Text(
                text =
                    "No tutor available for your lesson: go back to change your lesson or click on the button to wait for a tutor to choose your lesson.",
                modifier = Modifier.align(Alignment.Center).testTag("noTutorMessage"))
          } else {
            DisplayTutors(
                modifier =
                    Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .testTag("tutorsList"),
                tutors = filteredTutor,
                onCardClick = { tutor ->
                    listProfilesViewModel.selectProfile(tutor)
                    navigationActions.navigateTo(Screen.SELECTED_TUTOR_DETAILS)
                })
          }
        }
      }
}

// TODO: fill this function
fun isTutorAvailable(tutorSchedule: List<List<Int>>, timeSlot: String): Boolean {
  return true
}
