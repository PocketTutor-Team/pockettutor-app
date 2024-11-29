package com.github.se.project.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.ListProfilesViewModel

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
                      IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowLeft,
                            contentDescription = if (expanded) "Collapse" else "Expand")
                      }
                    }
                  } else null,
              modifier =
                  if (!isInstant) {
                    Modifier.clickable { expanded = !expanded }
                  } else Modifier)

          if (expanded) {
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
