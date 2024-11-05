package com.github.se.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.lesson.LessonStatus

@Composable
fun DisplayLessons(
    modifier: Modifier = Modifier,
    lessons: List<Lesson>,
    statusFilter: LessonStatus? = null, // Option to filter by status
    isTutor: Boolean,
    onCardClick: (Lesson) -> Unit = {}
) {
  val filteredLessons =
      statusFilter?.let { lessons.filter { lesson -> lesson.status == it } } ?: lessons

  LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    itemsIndexed(filteredLessons) { index, lesson ->
      Card(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(vertical = 2.dp)
                  .testTag("lessonCard_$index")
                  .clickable { onCardClick(lesson) }, // Tag for each lesson card
          colors =
              CardDefaults.cardColors(
                  containerColor =
                      if (LessonStatus.COMPLETED == lesson.status) {
                        MaterialTheme.colorScheme.secondaryContainer
                      } else if (LessonStatus.PENDING == lesson.status ||
                          LessonStatus.REQUESTED == lesson.status) {
                        if (isSystemInDarkTheme()) {
                          MaterialTheme.colorScheme.primaryContainer
                        } else {
                          Color(0xFFFEDF89)
                        }
                      } else {
                        MaterialTheme.colorScheme.surfaceBright
                      })) {
            Column(
                modifier =
                    Modifier.padding(16.dp)
                        .fillMaxWidth()
                        .testTag("lessonContent_$index"), // Tag for the lesson content
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(
                      text = lesson.title,
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.testTag("lessonTitle_$index") // Tag for the lesson title
                      )

                  if (LessonStatus.COMPLETED == lesson.status ||
                      LessonStatus.CONFIRMED == lesson.status) {
                    Text(
                        text =
                            if (isTutor) "With: ${lesson.studentUid}"
                            else "With: ${lesson.tutorUid}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier =
                            Modifier.testTag(
                                "lessonParticipant_$index") // Tag for the tutor/student info
                        )
                  }

                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Price: ${lesson.price}.-",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier =
                                Modifier.testTag("lessonPrice_$index") // Tag for the lesson price
                            )

                        Text(
                            text = lesson.subject.name.lowercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier =
                                Modifier.testTag(
                                    "lessonSubject_$index") // Tag for the lesson subject
                            )

                        val formattedDate = lesson.timeSlot.take(10)
                        Text(
                            text = "Date: $formattedDate",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier =
                                Modifier.testTag("lessonDate_$index") // Tag for the lesson date
                            )
                      }
                }
          }
    }
  }
}
