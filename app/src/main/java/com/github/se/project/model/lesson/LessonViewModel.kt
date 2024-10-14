package com.android.sample.model.lesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing lessons and interacting with the LessonRepository. Handles the retrieval,
 * addition, and deletion of lessons.
 */
class LessonViewModel(private val repository: LessonRepository) : ViewModel() {
  private val lessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  val lessons: StateFlow<List<Lesson>> = lessons_.asStateFlow()

  private val selectedLesson_ = MutableStateFlow<Lesson?>(null)
  val selectedLesson: StateFlow<Lesson?> = selectedLesson_.asStateFlow()

  init {
    repository.init { getLessonsByUser(Firebase.auth.currentUser?.uid ?: "") }
  }

  /** Factory for creating a LessonsViewModel. */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LessonViewModel(LessonRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  /**
   * Retrieves all lessons associated with a specific user.
   *
   * @param userUid The UID of the user whose lessons are to be retrieved.
   */
  fun getLessonsByUser(userUid: String) {
    repository.getLessonsByUserId(
        userUid,
        onSuccess = { lessons_.value = it },
        onFailure = { e -> Log.e("LessonViewModel", "Error loading user: $userUid's lessons", e) })
  }

  /**
   * Adds a new lesson to the repository.
   *
   * @param userUid The UID of the user to associate with the lesson.
   * @param lesson The Lesson object to be added.
   */
  fun addLesson(userUid: String, lesson: Lesson) {
    repository.addLessonByUserId(
        userUid = userUid,
        lesson = lesson,
        onSuccess = { getLessonsByUser(userUid) }, // Refresh the lesson list on success
        onFailure = { Log.e("LessonViewModel", "Error adding lesson: $lesson", it) })
  }

  /**
   * Deletes a lesson from the repository.
   *
   * @param userUid The UID of the user associated with the lesson.
   * @param lessonId The ID of the lesson to be deleted.
   */
  fun deleteLesson(userUid: String, lessonId: String) {
    repository.deleteLessonByUserId(
        userUid = userUid,
        lessonId = lessonId,
        onSuccess = { getLessonsByUser(userUid) }, // Refresh the lesson list on success
        onFailure = { Log.e("LessonViewModel", "Error deleting lesson: $lessonId", it) })
  }

  /**
   * Selects a lesson to be used in the view.
   *
   * @param lesson The Lesson object to be selected.
   */
  fun selectLesson(lesson: Lesson) {
    selectedLesson_.value = lesson
  }
}
