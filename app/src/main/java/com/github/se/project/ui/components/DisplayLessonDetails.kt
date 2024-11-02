package com.github.se.project.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile

@Composable
fun DisplayLessonDetails(
    lesson: Lesson,
    studentProfile: Profile,
) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 2.dp)
              .testTag("lessonDetailsCard"), // Tag for each lesson card
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)) {
        Column(
            content = {
              // Student information
              Row(
                  modifier = Modifier.padding(16.dp).fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  content = {
                    // TODO: Add profile image

                    // Name and Academic Level
                    Column(
                        content = {
                          Text(
                              studentProfile.firstName + studentProfile.lastName,
                              style = MaterialTheme.typography.titleMedium,
                              modifier = Modifier.testTag("studentName"))
                          Text(
                              studentProfile.section.toString() +
                                  " " +
                                  studentProfile.academicLevel.toString(),
                              style = MaterialTheme.typography.bodyMedium,
                          )
                        })

                    // Price range
                    Text(lesson.minPrice.toString() + ".-/" + lesson.maxPrice.toString() + ".-")
                  })

              // Lesson details
              Column(
                  modifier = Modifier.padding(16.dp),
              ) {
                Text(
                    lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("lessonTitle"))
                Text(
                    lesson.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("lessonDescription"))
              }
            })
      }
}
