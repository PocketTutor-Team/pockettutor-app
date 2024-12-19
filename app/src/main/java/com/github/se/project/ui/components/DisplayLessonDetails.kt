package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile
import com.github.se.project.model.profile.Role
import com.github.se.project.utils.formatDate

/** Displays detailed information about a lesson and the associated student. */
@Composable
fun DisplayLessonDetails(lesson: Lesson, profile: Profile, modifier: Modifier = Modifier) {
  Card(
      modifier = modifier.fillMaxWidth().padding(vertical = 2.dp).testTag("lessonDetailsCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

              // Profile information section
              ProfileInfoSection(profile)

              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

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
                              modifier = Modifier.testTag("lessonTime"))
                        }
                  }

              // Lesson information section
              LessonInfoSection(lesson)
            }
      }
}

@Composable
private fun ProfileInfoSection(profile: Profile) {
  Row(
      modifier = Modifier.fillMaxWidth().testTag("profileInfoRow"),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Box(contentAlignment = Alignment.Center) {
          ProfilePhoto(
              photoUri = profile.profilePhotoUrl,
              size = 60.dp,
              showPlaceholder = true,
              modifier = Modifier.size(60.dp))

          if (profile.certification?.verified == true && profile.role == Role.TUTOR) {
            Surface(
                modifier =
                    Modifier.size(24.dp).align(Alignment.BottomEnd).offset(x = 8.dp, y = 8.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 2.dp) {
                  Surface(
                      modifier = Modifier.padding(2.dp).fillMaxSize(),
                      shape = CircleShape,
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.epflpng),
                            contentDescription = "EPFL Verified",
                            modifier = Modifier.padding(2.dp),
                            tint = Color.Red)
                      }
                }
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = "${profile.firstName} ${profile.lastName}",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.testTag("profileName"))

          Text(
              text = "${profile.section} - ${profile.academicLevel}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.testTag("profileAcademicInfo"))
        }

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
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.testTag("lessonDescription"),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3)
      }
}
