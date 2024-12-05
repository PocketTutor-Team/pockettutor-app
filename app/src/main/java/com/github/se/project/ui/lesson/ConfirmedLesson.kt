package com.github.se.project.ui.lesson

import android.content.Context
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
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.network.NetworkStatusViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions
import com.github.se.project.ui.navigation.Screen
import com.github.se.project.utils.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
private fun LessonActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String,
    icon: @Composable () -> Unit,
    isError: Boolean = false
) {
  Button(
      shape = MaterialTheme.shapes.medium,
      onClick = onClick,
      colors =
          if (isError) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          } else ButtonDefaults.buttonColors(),
      modifier = modifier.fillMaxWidth().padding(bottom = 16.dp).testTag(testTag)) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
      }
}

private fun Context.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

private fun navigateWithToast(
    navigationActions: NavigationActions,
    context: Context,
    message: String,
    screen: String = Screen.HOME
) {
  context.showToast(message)
  navigationActions.navigateTo(screen)
}

@Composable
private fun LessonScreenTitle(status: LessonStatus) {
  val title =
      when (status) {
        LessonStatus.CONFIRMED -> "Confirmed Lesson"
        LessonStatus.PENDING_TUTOR_CONFIRMATION -> "Pending Tutor Confirmation"
        LessonStatus.INSTANT_CONFIRMED -> "Confirmed Instant Lesson"
        LessonStatus.STUDENT_REQUESTED -> "Pending Student Confirmation"
        else -> "Lesson Details"
      }
  Text(text = title, style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedLessonScreen(
    listProfilesViewModel: ListProfilesViewModel =
        viewModel(factory = ListProfilesViewModel.Factory),
    lessonViewModel: LessonViewModel = viewModel(factory = LessonViewModel.Factory),
    networkStatusViewModel: NetworkStatusViewModel = viewModel(),
    navigationActions: NavigationActions,
    onLocationChecked: () -> Unit = {}
) {

  val isConnected = networkStatusViewModel.isConnected.collectAsState().value

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
            title = { LessonScreenTitle(lesson.status) })
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("confirmedLessonScreen"),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              LessonDetailsCard(lesson, otherProfile, onLocationChecked)

              Spacer(modifier = Modifier.weight(1f))

              when {
                lesson.status == LessonStatus.CONFIRMED -> {
                  MessageButton(otherProfile, lesson, isStudent, isConnected)
                  CancelLessonButton(
                      lesson,
                      currentProfile,
                      lessonViewModel,
                      navigationActions,
                      context,
                      isConnected)
                }
                lesson.status == LessonStatus.INSTANT_CONFIRMED -> {
                  MessageButton(otherProfile, lesson, isStudent, isConnected)
                }
                lesson.status == LessonStatus.PENDING_TUTOR_CONFIRMATION && isStudent -> {
                  DeleteLessonButton(
                      lesson,
                      currentProfile,
                      lessonViewModel,
                      navigationActions,
                      context,
                      isConnected)
                }
                lesson.status == LessonStatus.STUDENT_REQUESTED && !isStudent -> {
                  CancelRequestButton(
                      lesson,
                      currentProfile,
                      lessonViewModel,
                      navigationActions,
                      context,
                      isConnected)
                }
              }
            }
      }
}

@Composable
private fun LessonDetailsCard(
    lesson: Lesson,
    otherProfile: Profile,
    onLocationChecked: () -> Unit
) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              DisplayLessonDetails(lesson, otherProfile)
              LessonLocationDisplay(
                  latitude = lesson.latitude,
                  longitude = lesson.longitude,
                  lessonTitle = lesson.title,
                  onLocationChecked = onLocationChecked)
            }
      }
}

@Composable
private fun MessageButton(
    otherProfile: Profile,
    lesson: Lesson,
    isStudent: Boolean,
    isConnected: Boolean
) {
  val context = LocalContext.current
  LessonActionButton(
      text = "Message ${if (isStudent) "Tutor" else "Student"}",
      onClick = {
        if (isConnected) {
          val intent =
              Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${otherProfile.phoneNumber}")
                putExtra("sms_body", "Hello, about our lesson ${formatDate(lesson.timeSlot)}...")
              }
          context.startActivity(intent)
        } else {
          Toast.makeText(
                  context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT)
              .show()
        }
      },
      testTag = "contactButton",
      icon = {
        Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = null,
            modifier = Modifier.size(20.dp))
      })
}

@Composable
private fun DeleteLessonButton(
    lesson: Lesson,
    currentProfile: Profile,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
    context: Context,
    isConnected: Boolean
) {
  LessonActionButton(
      text = "Cancel Lesson",
      onClick = {
        if (isConnected) {
          lessonViewModel.deleteLesson(
              lesson.id,
              onComplete = {
                lessonViewModel.getLessonsForStudent(currentProfile.uid) {}
                navigateWithToast(navigationActions, context, "Lesson cancelled successfully")
              })
        } else {
          Toast.makeText(
                  context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT)
              .show()
        }
      },
      testTag = "deleteButton",
      icon = {
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
      },
      isError = true)
}

@Composable
private fun CancelRequestButton(
    lesson: Lesson,
    currentProfile: Profile,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
    context: Context,
    isConnected: Boolean
) {
  LessonActionButton(
      text = "Cancel your request",
      onClick = {
        if (isConnected) {
          lessonViewModel.updateLesson(
              lesson.copy(tutorUid = lesson.tutorUid.filter { it != currentProfile.uid }),
              onComplete = {
                lessonViewModel.getLessonsForTutor(currentProfile.uid) {}
                navigateWithToast(navigationActions, context, "Request cancelled successfully")
              })
        } else {
          Toast.makeText(
                  context, context.getString(R.string.inform_user_offline), Toast.LENGTH_SHORT)
              .show()
        }
      },
      testTag = "cancelRequestButton",
      icon = {
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
      },
      isError = true)
}

@Composable
private fun CancelLessonButton(
    lesson: Lesson,
    currentProfile: Profile,
    lessonViewModel: LessonViewModel,
    navigationActions: NavigationActions,
    context: Context,
    isConnected: Boolean
) {
  var showCancelDialog by remember { mutableStateOf(false) }

  LessonActionButton(
      text = "Cancel Lesson",
      onClick = { showCancelDialog = true },
      testTag = "cancelButton",
      icon = {
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
      },
      isError = true)

  if (showCancelDialog) {
    AlertDialog(
        modifier = Modifier.testTag("cancelDialog"),
        onDismissRequest = { showCancelDialog = false },
        title = {
          Text(text = "Lesson Cancellation", modifier = Modifier.testTag("cancelDialogTitle"))
        },
        text = {
          Text(
              text = "Are you sure you want to cancel the lesson? This action can not be undone.",
              modifier = Modifier.testTag("cancelDialogText"))
        },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("cancelDialogConfirmButton"),
              onClick = {
                // Check if the user is connected to the internet
                if (isConnected) {
                  // If the lesson is within 24 hours, do not allow cancellation
                  // Otherwise, update the lesson status and refresh the list of lessons
                  if (isCancellationValid(lesson.timeSlot)) {
                    if (currentProfile.role == Role.STUDENT) {
                      lessonViewModel.updateLesson(
                          lesson = lesson.copy(status = LessonStatus.STUDENT_CANCELLED),
                          onComplete = { lessonViewModel.getLessonsForStudent(currentProfile.uid) })
                    } else {
                      lessonViewModel.updateLesson(
                          lesson = lesson.copy(status = LessonStatus.TUTOR_CANCELLED),
                          onComplete = { lessonViewModel.getLessonsForTutor(currentProfile.uid) })
                    }
                    showCancelDialog = false
                    navigateWithToast(navigationActions, context, "Lesson cancelled successfully")
                  } else {
                    context.showToast("You can only cancel a lesson 24 hours before it starts")
                    showCancelDialog = false
                  }
                  // when the user is offline inform with a toast message
                } else {
                  Toast.makeText(
                          context,
                          context.getString(R.string.inform_user_offline),
                          Toast.LENGTH_SHORT)
                      .show()
                }
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
