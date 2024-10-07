package com.android.sample

interface LessonRepository {

    // Method to initialize the repository
    fun init(onSuccess: () -> Unit)

    // Method to add a new lesson
    fun addLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Method to retrieve a lesson by its ID
    fun getLessonById(lessonId: String, onSuccess: (Lesson) -> Unit, onFailure: (Exception) -> Unit)

    // Method to retrieve all lessons by tutor UID
    fun getLessonsByUser(userUid: String, onSuccess: (List<Lesson>) -> Unit, onFailure: (Exception) -> Unit)

    // Method to update an existing lesson
    fun updateLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    // Method to delete a lesson by its ID
    fun deleteLesson(lessonId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}