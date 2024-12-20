package com.github.se.project.ui.lesson

import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.R
import com.github.se.project.model.chat.ChatViewModel
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
    isError: Boolean = false,
    isActive: Boolean = true
) {
  Button(
      shape = MaterialTheme.shapes.medium,
      onClick = onClick,
      colors =
          if (!isActive) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
          } else if (isError) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          } else {
            ButtonDefaults.buttonColors()
          },
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
        LessonStatus.CONFIRMED -> stringResource(R.string.confirmed_lesson)
        LessonStatus.PENDING_TUTOR_CONFIRMATION ->
            stringResource(R.string.pending_tutor_confirmation)
        LessonStatus.INSTANT_CONFIRMED -> stringResource(R.string.confirmed_instant_lesson)
        LessonStatus.STUDENT_REQUESTED -> stringResource(R.string.pending_student_confirmation)
        else -> stringResource(R.string.lesson_details)
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
    chatViewModel: ChatViewModel,
    onLocationChecked: () -> Unit = {}
) {

  val isConnected = networkStatusViewModel.isConnected.collectAsState().value

  val currentProfile =
      listProfilesViewModel.currentProfile.collectAsState().value
          ?: return Text(stringResource(R.string.no_profile_selected))
  val isStudent = currentProfile.role == Role.STUDENT

  val lesson =
      lessonViewModel.selectedLesson.collectAsState().value
          ?: return Text(stringResource(R.string.no_lesson_selected))

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
                  MessageButton(lesson, isStudent, chatViewModel, navigationActions)
                  CancelLessonButton(
                      lesson,
                      currentProfile,
                      lessonViewModel,
                      navigationActions,
                      context,
                      isConnected)
                }
                lesson.status == LessonStatus.INSTANT_CONFIRMED -> {
                  MessageButton(lesson, isStudent, chatViewModel, navigationActions)
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
    lesson: Lesson,
    isStudent: Boolean,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions
) {

  LessonActionButton(
      text = "Message ${if (isStudent) "Tutor" else "Student"}",
      onClick = {
        chatViewModel.createOrGetChannel(
            lesson,
            { channel ->
              chatViewModel.setCurrentChannelId(channel.cid)
              navigationActions.navigateTo(Screen.CHAT)
            },
            lesson.title)
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
      text = stringResource(R.string.cancel_lesson),
      onClick = {
        if (isConnected) {
          lessonViewModel.deleteLesson(
              lesson.id,
              onComplete = {
                lessonViewModel.getLessonsForStudent(currentProfile.uid) {}
                navigateWithToast(
                    navigationActions, context, context.getString(R.string.lesson_canceled))
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
      text = stringResource(R.string.cancel_request),
      onClick = {
        if (isConnected) {
          lessonViewModel.updateLesson(
              lesson.copy(tutorUid = lesson.tutorUid.filter { it != currentProfile.uid }),
              onComplete = {
                lessonViewModel.getLessonsForTutor(currentProfile.uid) {}
                navigateWithToast(
                    navigationActions, context, context.getString(R.string.request_canceled))
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
      text = stringResource(R.string.cancel_lesson),
      onClick = { showCancelDialog = true },
      testTag = "cancelButton",
      icon = {
        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
      },
      isError = true,
      isActive = isCancellationValid(lesson.timeSlot))

  if (showCancelDialog) {
    AlertDialog(
        modifier = Modifier.testTag("cancelDialog"),
        onDismissRequest = { showCancelDialog = false },
        title = {
          Text(text = "Lesson Cancellation", modifier = Modifier.testTag("cancelDialogTitle"))
        },
        text = {
          Text(
              text = stringResource(R.string.cancel_warning),
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
                    navigateWithToast(
                        navigationActions, context, context.getString(R.string.successful_cancel))
                  } else {
                    context.showToast(context.getString(R.string.failed_cancel))
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
                Text(stringResource(R.string.confirm_cancel))
              }
        },
        dismissButton = {
          Button(
              modifier = Modifier.testTag("cancelDialogDismissButton"),
              onClick = { showCancelDialog = false }) {
                Text(stringResource(R.string.no))
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
