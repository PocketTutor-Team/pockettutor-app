package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Profile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DisplayLessons(
    modifier: Modifier = Modifier,
    lessons: List<Lesson>,
    statusFilter: LessonStatus? = null,
    isTutor: Boolean,
    tutorEmpty: Boolean = false,
    onCardClick: (Lesson) -> Unit = {},
    listProfilesViewModel: ListProfilesViewModel // Ajout du ViewModel pour récupérer les profils
) {
  val filteredLessons =
      statusFilter?.let { lessons.filter { lesson -> lesson.status == it && lesson.tutorUid.isEmpty() == tutorEmpty} } ?: lessons

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    filteredLessons.forEachIndexed { index, lesson ->
      var otherUserProfile by remember { mutableStateOf<Profile?>(null) }

      LaunchedEffect(lesson) {
        otherUserProfile =
            if (isTutor) {
              listProfilesViewModel.getProfileById(lesson.studentUid)
            } else {
              listProfilesViewModel.getProfileById(lesson.tutorUid.toString())
            }
      }

      Card(
          modifier =
              Modifier.fillMaxWidth().testTag("lessonCard_$index").clickable {
                onCardClick(lesson)
              },
          colors =
              CardDefaults.cardColors(
                  containerColor =
                      when {
                        lesson.status == LessonStatus.COMPLETED ->
                            MaterialTheme.colorScheme.secondaryContainer
                        lesson.status == LessonStatus.STUDENT_REQUESTED ->
                            if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer
                            else Color(0xFFFEDF89)
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                      }),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("lessonContent_$index"),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f).testTag("lessonTitle_$index"))
                        Text(
                            text = lesson.subject.name.lowercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("lessonSubject_$index"))
                      }

                  Text(
                      text = formatDateTime(lesson.timeSlot),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.testTag("lessonDate_$index"))

                  if (lesson.status == LessonStatus.COMPLETED ||
                      lesson.status == LessonStatus.CONFIRMED) {
                    Text(
                        text =
                            if (isTutor)
                                "Student: ${otherUserProfile?.firstName ?: "Unknown"} ${otherUserProfile?.lastName ?: ""}"
                            else
                                "Tutor: ${otherUserProfile?.firstName ?: "Unknown"} ${otherUserProfile?.lastName ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("lessonParticipant_$index"))
                  }
                }
          }
    }
  }
}

private fun formatDateTime(timeSlot: String): String {
  return try {
    val dateTime =
        LocalDateTime.parse(timeSlot, DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
    dateTime.format(DateTimeFormatter.ofPattern("EEEE, d MMMM • HH:mm"))
  } catch (e: Exception) {
    timeSlot
  }
}
