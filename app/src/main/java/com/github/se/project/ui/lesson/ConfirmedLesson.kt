package com.github.se.project.ui.lesson

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.project.model.lesson.LessonViewModel
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
import com.github.se.project.ui.components.DisplayLessonDetails
import com.github.se.project.ui.components.LessonLocationDisplay
import com.github.se.project.ui.navigation.NavigationActions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
              Text(text = "Confirmed Lesson", style = MaterialTheme.typography.titleLarge)
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

              // Contact Button
              Button(
                  shape = MaterialTheme.shapes.medium,
                  onClick = {
                    val intent =
                        Intent(Intent.ACTION_VIEW).apply {
                          data = Uri.parse("sms:${otherProfile.phoneNumber}")
                          putExtra(
                              "sms_body",
                              "Hello, about our lesson ${formatDateTime(lesson.timeSlot)}...")
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
      }
}

private fun formatDateTime(timeSlot: String): String {
  return try {
    val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
    val dateTime = LocalDateTime.parse(timeSlot, pattern)

    val dayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    "on ${dateTime.format(dayFormatter)} at ${dateTime.format(timeFormatter)}"
  } catch (e: Exception) {
    "on $timeSlot"
  }
}
