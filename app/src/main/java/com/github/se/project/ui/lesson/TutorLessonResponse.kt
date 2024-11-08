package com.github.se.project.ui.lesson

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen

/** Screen where tutors can respond to lesson requests by setting their price. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorLessonResponseScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    navigationActions: NavigationActions,
) {
  val lesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text("No lesson selected. Should not happen.")

  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text("No profile found. Should not happen.")

  var showConfirmDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current

  var showDeclineDialog by remember { mutableStateOf(false) }

  Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        TopAppBar(
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
              if (lesson.status == LessonStatus.STUDENT_REQUESTED) {
                Text(
                    text = "Confirm the Lesson",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("confirmLessonTitle"))
              } else {
                Text(
                    text = "Respond to Request",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("requestPendingLessonTitle"))
              }
            })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("tutorLessonResponseScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              val studentProfile = listProfilesViewModel.getProfileById(lesson.studentUid)
              if (studentProfile == null) {
                ErrorState(message = "Cannot retrieve student profile")
              } else {
                // Lesson information card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                      DisplayLessonDetails(lesson, studentProfile)

                      LessonLocationDisplay(
                          latitude = lesson.latitude,
                          longitude = lesson.longitude,
                          lessonTitle = lesson.title)
                    }

                Spacer(modifier = Modifier.weight(1f))

                // Confirmation button
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showConfirmDialog = true },
                    modifier =
                        Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("confirmButton")) {
                      Icon(
                          Icons.Default.Send,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Offer to Teach (${currentProfile.price}.-/hour)")
                    }

                if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION) {
                  Button(
                      shape = MaterialTheme.shapes.medium,
                      onClick = { showDeclineDialog = true },
                      modifier =
                          Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("cancelButton"),
                      colors =
                          ButtonDefaults.buttonColors(
                              containerColor = MaterialTheme.colorScheme.error)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Dismiss the Lesson")
                      }
                }
              }
            }

        // Confirmation Dialog
        if (showConfirmDialog) {
          AlertDialog(
              onDismissRequest = { showConfirmDialog = false },
              title = { Text("Confirm Your Offer") },
              text = {
                Text(
                    "Would you like to offer to teach this lesson at your standard rate of ${currentProfile.price}.-/hour?")
              },
              confirmButton = {
                Button(
                    onClick = {
                      lessonViewModel.updateLesson(
                          lesson.copy(
                              tutorUid = lesson.tutorUid + currentProfile.uid,
                              price = currentProfile.price.toDouble(),
                              status =
                                  if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION)
                                      LessonStatus.CONFIRMED
                                  else LessonStatus.STUDENT_REQUESTED,
                          ),
                          onComplete = {
                            lessonViewModel.getLessonsForTutor(currentProfile.uid)
                            lessonViewModel.getAllRequestedLessons()
                            Toast.makeText(context, "Offer sent successfully!", Toast.LENGTH_SHORT)
                                .show()
                            navigationActions.navigateTo(Screen.HOME)
                          })
                    }) {
                      Text("Confirm")
                    }
              },
              dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
              })
        }

        // Decline Dialog
        if (showDeclineDialog) {
          AlertDialog(
              onDismissRequest = { showDeclineDialog = false },
              title = { Text("Dismiss the Lesson") },
              text = { Text("Are you sure you want to dismiss this lesson?") },
              confirmButton = {
                Button(
                    onClick = {
                      lessonViewModel.updateLesson(
                          lesson.copy(
                              tutorUid = listOf(),
                              status = LessonStatus.STUDENT_REQUESTED,
                          ),
                          onComplete = {
                            lessonViewModel.getLessonsForTutor(currentProfile.uid)
                            Toast.makeText(
                                    context, "Lesson dismiss successfully.", Toast.LENGTH_SHORT)
                                .show()
                            navigationActions.navigateTo(Screen.HOME)
                          })
                    }) {
                      Text("Confirm")
                    }
              },
              dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) { Text("Cancel") }
              })
        }
      }
}

@Composable
private fun ErrorState(message: String) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error)
      }
}
