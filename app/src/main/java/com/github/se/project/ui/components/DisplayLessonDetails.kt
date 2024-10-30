package com.github.se.project.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.se.project.model.lesson.Lesson
import com.github.se.project.model.profile.Profile

@Composable
fun DisplayLessonDetails(
    lesson: Lesson,
    studentProfile: Profile,
) {
  Column(
      modifier = Modifier.fillMaxSize(),
      content = {
        // Student information
        Row(
            content = {
              // TODO: Add profile image

              // Name and Academic Level
              Column(
                  content = {
                    Text(studentProfile.firstName + studentProfile.lastName)
                    Text(
                        studentProfile.section.toString() +
                            " " +
                            studentProfile.academicLevel.toString())
                  })

              // Price range
              Text(lesson.minPrice.toString() + ".-/" + lesson.maxPrice.toString() + ".-")
            })

        // Lesson details
        Text(lesson.title)
        Text(lesson.description)
      })
}
