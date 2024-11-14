package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile
import com.github.se.project.utils.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Displays detailed information about a lesson and the associated student. */
@Composable
fun DisplayLessonDetails(lesson: Lesson, studentProfile: Profile, modifier: Modifier = Modifier) {
  Card(
      modifier = modifier.fillMaxWidth().padding(vertical = 2.dp).testTag("lessonDetailsCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
              // Student information section
              StudentInfoSection(studentProfile)

              Divider(color = MaterialTheme.colorScheme.outlineVariant)

              // Lesson information section
              LessonInfoSection(lesson)

              Divider(color = MaterialTheme.colorScheme.outlineVariant)

              // Time and date section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = formatDate(lesson.timeSlot),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("lessonDate"))
                }
            }
        }
      }
}

@Composable
private fun StudentInfoSection(profile: Profile) {
  Row(
      modifier = Modifier.fillMaxWidth().testTag("studentInfoRow"),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        // Profile picture placeholder
        Surface(
            modifier = Modifier.size(48.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
              Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  modifier = Modifier.padding(8.dp),
                  tint = MaterialTheme.colorScheme.primary)
            }

        // Student details
        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = "${profile.firstName} ${profile.lastName}",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("studentName"))
          Text(
              text = "${profile.section} - ${profile.academicLevel}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("studentAcademicInfo"))
        }

        // Languages
        Column(horizontalAlignment = Alignment.End) {
          profile.languages.forEach { language ->
            Text(
                text = language.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
          }
        }
      }
}

@Composable
private fun LessonInfoSection(lesson: Lesson) {
  Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.testTag("lessonDetailsColumn")) {
        // Subject and Title
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)) {
                CustomIcon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_school_24),
                    contentDescription = "School Icon")
                Text(
                    text = lesson.subject.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.testTag("lessonSubject"))
              }
        }

        // Title
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("lessonTitle"))

        // Description
        Text(
            text = lesson.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("lessonDescription"),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3)
      }
}
