package com.github.se.project.ui.lesson

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedLessonScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions
) {
  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text("No profile found. Should not happen.")
  val isStudent = currentProfile.role == Role.STUDENT
  val lesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen.")

  val otherProfile =
      if (isStudent) {
        listProfilesViewModel.getProfileById(lesson.tutorUid[0])
      } else {
        listProfilesViewModel.getProfileById(lesson.studentUid)
      } ?: return Text("Cannot retrieve profile")

  val context = LocalContext.current
  Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        TopAppBar(
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("backButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back")
                  }
            },
            title = {
              if (lesson.status == LessonStatus.CONFIRMED) {
                Text(text = "Confirmed Lesson", style = MaterialTheme.typography.titleLarge)
              } else if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION) {
                Text(
                    text = "Pending Tutor Confirmation",
                    style = MaterialTheme.typography.titleLarge)
              } else if (lesson.status == LessonStatus.INSTANT_CONFIRMED) {
                Text(text = "Confirmed Instant Lesson", style = MaterialTheme.typography.titleLarge)
              } else if (lesson.status == LessonStatus.STUDENT_REQUESTED) {
                Text(
                    text = "Pending Student Confirmation",
                    style = MaterialTheme.typography.titleLarge)
              }
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("confirmedLessonScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              // Lesson Details Card
              Card(
                  modifier = Modifier.fillMaxWidth(),
                  colors =
                      CardDefaults.cardColors(
                          containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                          DisplayLessonDetails(lesson, otherProfile)

                          LessonLocationDisplay(
                              latitude = lesson.latitude,
                              longitude = lesson.longitude,
                              lessonTitle = lesson.title)
                        }
                  }

              Spacer(modifier = Modifier.weight(1f))

              // This button require a context when testing
              // Contact Button
              if (lesson.status == LessonStatus.CONFIRMED || lesson.status == LessonStatus.INSTANT_CONFIRMED) {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                      val intent =
                          Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("sms:${otherProfile.phoneNumber}")
                            putExtra(
                                "sms_body",
                                "Hello, about our lesson ${formatDate(lesson.timeSlot)}...")
                          }
                      context.startActivity(intent)
                    },
                    modifier =
                        Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("contactButton")) {
                      Icon(
                          Icons.Default.Send,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Message ${if (isStudent) "Tutor" else "Student"}")
                    }
              }

              if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION && isStudent) {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                      lessonViewModel.deleteLesson(
                          lesson.id,
                          onComplete = {
                            lessonViewModel.getLessonsForStudent(
                                currentProfile.uid, onComplete = {})
                            navigationActions.navigateTo(Screen.HOME)
                            Toast.makeText(
                                    context, "Lesson cancelled successfully", Toast.LENGTH_SHORT)
                                .show()
                          })
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error),
                    modifier =
                        Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("cancelButton")) {
                      Icon(
                          Icons.Default.Close,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Cancel Lesson")
                    }
              }

              if (lesson.status == LessonStatus.STUDENT_REQUESTED && !isStudent) {
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                      lessonViewModel.updateLesson(
                          lesson.copy(
                              tutorUid = lesson.tutorUid.filter { it != currentProfile.uid },
                          ),
                          onComplete = {
                            lessonViewModel.getLessonsForTutor(currentProfile.uid, onComplete = {})
                            navigationActions.navigateTo(Screen.HOME)
                            Toast.makeText(
                                    context, "Request cancelled successfully", Toast.LENGTH_SHORT)
                                .show()
                          })
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error),
                    modifier =
                        Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("cancelButton")) {
                      Icon(
                          Icons.Default.Close,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Cancel your request")
                    }
              }
            }
      }
}
