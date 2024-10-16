package com.github.se.project.model.lesson

import com.android.sample.model.lesson.Lesson

interface LessonRepository {

  // Method to initialize the repository
  fun init(onSuccess: () -> Unit)

  // Retrieve all lessons for a specific tutor
  fun getLessonsByTutorUid(
      tutorUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Retrieve all lessons for a specific student
  fun getLessonsByStudentUid(
      studentUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Method to add a new lesson
  fun addLessonByUserId(
      userUid: String,
      lesson: Lesson,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Method to update an existing lesson by its ID
  fun updateLessonByUserId(
      userUid: String,
      lesson: Lesson,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Method to delete a lesson by its ID
  fun deleteLessonByUserId(
      userUid: String,
      lessonId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
