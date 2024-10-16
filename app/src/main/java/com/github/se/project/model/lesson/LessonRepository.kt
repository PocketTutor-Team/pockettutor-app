package com.android.sample.model.lesson

interface LessonRepository {
  // Method to get a new unique identifier
  fun getNewUid(): String

  // Method to initialize the repository
  fun init(onSuccess: () -> Unit)

  // Method to retrieve all lessons by user UID
  fun getLessons(onSuccess: (List<Lesson>) -> Unit, onFailure: (Exception) -> Unit)

  // Method to retrieve all lessons by user UID
  fun getLessonsByUserId(
      userUid: String,
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
