package com.github.se.project.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.project.R
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.utils.capitalizeFirstLetter

@Composable
fun ExpandableLessonSection(
    section: SectionInfo,
    lessons: List<Lesson>,
    isTutor: Boolean,
    onClick: (Lesson) -> Unit,
    listProfilesViewModel: ListProfilesViewModel
) {
  val isInstant = lessons.any { isInstant(it) }
  var expanded by remember { mutableStateOf(if (isInstant) true else lessons.isNotEmpty()) }
  val lessonCountText = lessons.size.toString()

  LaunchedEffect(lessons.isNotEmpty()) { expanded = lessons.isNotEmpty() }

  val infiniteTransition = rememberInfiniteTransition(label = "iconBlink")
  val alpha by
      infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 0.3f,
          animationSpec =
              infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse),
          label = "blinkAlpha")

  Card(
      modifier = Modifier.fillMaxWidth().testTag("section_${section.title}"),
      colors =
          CardDefaults.cardColors(
              containerColor =
                  if (isInstant) MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f)
                  else MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column {
          ListItem(
              headlineContent = {
                Text(section.title, style = MaterialTheme.typography.titleMedium)
              },
              colors =
                  ListItemDefaults.colors(
                      containerColor =
                          if (isInstant) MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.4f)
                          else MaterialTheme.colorScheme.surfaceContainerLowest),
              leadingContent = {
                Icon(
                    imageVector = section.icon,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isInstant) alpha else 1f))
              },
              trailingContent =
                  if (!isInstant) {
                    {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        if (lessonCountText.isNotEmpty()) {
                          if (lessons.size > 1) {
                            Text(
                                text = "$lessonCountText lessons",
                                style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                          } else {
                            Text(
                                text = "$lessonCountText lesson",
                                style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                          }
                        }
                        IconButton(onClick = { expanded = !expanded }) {
                          Icon(
                              imageVector =
                                  if (expanded) Icons.Default.KeyboardArrowDown
                                  else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                              contentDescription = if (expanded) "Collapse" else "Expand")
                        }
                      }
                    }
                  } else null,
              modifier =
                  if (!isInstant) {
                    Modifier.testTag("section_${section.status.name}_expand").clickable {
                      expanded = !expanded
                    }
                  } else Modifier.testTag("section_${section.status.name}_expand"))

          if (expanded) {
            if (lessons.isEmpty()) {
              Text(
                  text = stringResource(R.string.no_lessons),
                  style = MaterialTheme.typography.bodyMedium,
                  modifier =
                      Modifier.padding(16.dp)
                          .testTag("noLessons" + section.status.name.capitalizeFirstLetter()))
            } else {
              DisplayLessons(
                  lessons = lessons,
                  statusFilter = section.status,
                  isTutor = isTutor,
                  tutorEmpty = section.tutorEmpty,
                  onCardClick = onClick,
                  modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                  listProfilesViewModel = listProfilesViewModel)
            }
          }
        }
      }
}
