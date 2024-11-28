package com.github.se.project.ui.lesson

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.github.se.project.utils.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

  var showCancelDialog by remember { mutableStateOf(false) }

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
              Text(
                  text =
                      when (lesson.status) {
                        LessonStatus.CONFIRMED -> "Confirmed Lesson"
                        LessonStatus.PENDING_TUTOR_CONFIRMATION -> "Pending Lesson"
                        LessonStatus.STUDENT_REQUESTED -> "Requested Lesson"
                        else -> "Lesson Details"
                      },
                  style = MaterialTheme.typography.titleLarge)
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

              if (lesson.status == LessonStatus.CONFIRMED) {
                // This button require a context when testing
                // Contact Button
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
                          Icons.AutoMirrored.Filled.Send,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Message ${if (isStudent) "Tutor" else "Student"}")
                    }
              }

              if (lesson.status == LessonStatus.CONFIRMED ||
                  (lesson.status == LessonStatus.STUDENT_REQUESTED &&
                      currentProfile.role == Role.TUTOR) ||
                  (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION &&
                      currentProfile.role == Role.STUDENT)) {
                // Cancellation Button
                Button(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { showCancelDialog = true },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("cancellationButton"),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error)) {
                      Icon(
                          Icons.Default.Close,
                          contentDescription = null,
                          modifier = Modifier.size(20.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text("Cancel the Lesson")
                    }
              }
            }

        if (showCancelDialog) {
          AlertDialog(
              modifier = Modifier.testTag("cancelDialog"),
              onDismissRequest = { showCancelDialog = false },
              title = {
                Text(text = "Lesson Cancellation", modifier = Modifier.testTag("cancelDialogTitle"))
              },
              text = {
                Text(
                    text =
                        if (lesson.status == LessonStatus.CONFIRMED ||
                            (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION &&
                                currentProfile.role == Role.STUDENT))
                            "Are you sure you want to cancel the lesson? This action can not be undone."
                        else if (lesson.status == LessonStatus.STUDENT_REQUESTED &&
                            currentProfile.role == Role.TUTOR)
                            "Are you sure you want to cancel your proposition for the lesson?"
                        else "Should not happen.",
                    modifier = Modifier.testTag("cancelDialogText"))
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("cancelDialogConfirmButton"),
                    onClick = {
                      if (lesson.status == LessonStatus.CONFIRMED) {
                        // If the lesson is within 24 hours, do not allow cancellation
                        // Otherwise, update the lesson status and refresh the list of lessons
                        if (isCancellationValid(lesson.timeSlot)) {
                          if (isStudent) {
                            lessonViewModel.updateLesson(
                                lesson = lesson.copy(status = LessonStatus.STUDENT_CANCELLED),
                                onComplete = {
                                  lessonViewModel.getLessonsForStudent(currentProfile.uid)
                                })
                          } else {
                            lessonViewModel.updateLesson(
                                lesson = lesson.copy(status = LessonStatus.TUTOR_CANCELLED),
                                onComplete = {
                                  lessonViewModel.getLessonsForTutor(currentProfile.uid)
                                })
                          }
                        } else {
                          Toast.makeText(
                                  context,
                                  "You can only cancel a lesson 24 hours before it starts",
                                  Toast.LENGTH_LONG)
                              .show()
                          showCancelDialog = false
                        }
                      } else if (lesson.status == LessonStatus.STUDENT_REQUESTED &&
                          currentProfile.role == Role.TUTOR) {
                        // Remove the tutor from the lesson tutor list and refresh the list of
                        // lessons
                        lessonViewModel.updateLesson(
                            lesson =
                                lesson.copy(tutorUid = lesson.tutorUid.minus(currentProfile.uid)),
                            onComplete = { lessonViewModel.getLessonsForTutor(currentProfile.uid) })
                      } else if (lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION &&
                          currentProfile.role == Role.STUDENT) {
                        // Delete the lesson and refresh the list of lessons
                        lessonViewModel.deleteLesson(
                            lesson.id,
                            onComplete = {
                              lessonViewModel.getLessonsForStudent(currentProfile.uid)
                            })
                      }
                      showCancelDialog = false
                      navigationActions.goBack()
                    }) {
                      Text("Yes, cancel it")
                    }
              },
              dismissButton = {
                Button(
                    modifier = Modifier.testTag("cancelDialogDismissButton"),
                    onClick = { showCancelDialog = false }) {
                      Text("No")
                    }
              })
        }
      }
}

/**
 * Checks if the time slot of the lesson is valid for cancellation. The lesson can be cancelled if
 * it is more than 24 hours away.
 *
 * @param timeSlot The time slot of the lesson.
 * @return True if the lesson can be cancelled, false otherwise.
 */
private fun isCancellationValid(timeSlot: String): Boolean {
  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
  val lessonDateTime = LocalDateTime.parse(timeSlot, formatter)
  val currentDateTime = LocalDateTime.now()
  val currentDateTimePlus24Hours = currentDateTime.plus(24, ChronoUnit.HOURS)

  return lessonDateTime.isAfter(currentDateTimePlus24Hours)
}
