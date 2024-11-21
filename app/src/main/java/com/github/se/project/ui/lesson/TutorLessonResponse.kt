package com.github.se.project.ui.lesson

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.TextButton
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
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.ErrorState
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.components.isInstant
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
  Log.e("InstantTesting", "Response Setup")

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
                    .testTag("tutorLessonResponseScreen")) {
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
                          lessonTitle = lesson.title,
                      )
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
              modifier = Modifier.testTag("confirmDialog"),
              onDismissRequest = { showConfirmDialog = false },
              title = {
                Text(text = "Confirm Your Offer", modifier = Modifier.testTag("confirmDialogTitle"))
              },
              text = {
                Text(
                    "Would you like to offer to teach this lesson at your standard rate of ${currentProfile.price}.-/hour?",
                    modifier = Modifier.testTag("confirmDialogText"))
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("confirmDialogConfirmButton"),
                    onClick = {
                      if (isInstant(lesson) &&
                          lessonViewModel.currentUserLessons.value.any {
                            it.status == LessonStatus.INSTANT_CONFIRMED
                          }) {
                        Toast.makeText(
                                context,
                                "You already have an instant lesson scheduled!",
                                Toast.LENGTH_SHORT)
                            .show()
                      } else
                          lessonViewModel.updateLesson(
                              lesson.copy(
                                  tutorUid = lesson.tutorUid + currentProfile.uid,
                                  price = currentProfile.price.toDouble(),
                                  status =
                                      if (lesson.status ==
                                          LessonStatus.PENDING_TUTOR_CONFIRMATION) {
                                        LessonStatus.CONFIRMED
                                      } else if (isInstant(lesson)) {
                                        LessonStatus.INSTANT_CONFIRMED
                                      } else {
                                        LessonStatus.STUDENT_REQUESTED
                                      },
                              ),
                              onComplete = {
                                lessonViewModel.getLessonsForTutor(currentProfile.uid)
                                lessonViewModel.getAllRequestedLessons()
                                Toast.makeText(
                                        context, "Offer sent successfully!", Toast.LENGTH_SHORT)
                                    .show()
                                navigationActions.navigateTo(Screen.HOME)
                              })
                    }) {
                      Text("Confirm")
                    }
              },
              dismissButton = {
                TextButton(
                    modifier = Modifier.testTag("confirmDialogCancelButton"),
                    onClick = { showConfirmDialog = false }) {
                      Text("Cancel")
                    }
              })
        }

        // Decline Dialog
        if (showDeclineDialog) {
          AlertDialog(
              modifier = Modifier.testTag("declineDialog"),
              onDismissRequest = { showDeclineDialog = false },
              title = {
                Text(text = "Dismiss the Lesson", modifier = Modifier.testTag("declineDialogTitle"))
              },
              text = {
                Text(
                    text = "Are you sure you want to dismiss this lesson?",
                    modifier = Modifier.testTag("declineDialogText"))
              },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("declineDialogConfirmButton"),
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
                TextButton(
                    modifier = Modifier.testTag("declineDialogCancelButton"),
                    onClick = { showDeclineDialog = false }) {
                      Text("Cancel")
                    }
              })
        }
      }
}
