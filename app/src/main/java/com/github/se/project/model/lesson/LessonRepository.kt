package com.github.se.project.model.lesson

interface LessonRepository {
  // Method to get a new unique identifier
  fun getNewUid(): String

  // Method to initialize the repository
  fun init(onSuccess: () -> Unit)

  // Method to retrieve all lessons for a tutor
  fun getLessonsForTutor(
      tutorUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Method to retrieve all lessons for a student
  fun getLessonsForStudent(
      studentUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  // Method to add a new lesson
  fun addLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  // Method to update an existing lesson
  fun updateLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  // Method to delete a lesson by its ID
  fun deleteLesson(lessonId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
