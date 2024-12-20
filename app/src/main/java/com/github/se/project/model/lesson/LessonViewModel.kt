package com.github.se.project.model.lesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing lessons and interacting with the LessonRepository. Handles the retrieval,
 * addition, and deletion of lessons.
 */
open class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

  private val requestedLessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  open val requestedLessons: StateFlow<List<Lesson>> = requestedLessons_.asStateFlow()

  private val _currentUserLessons = MutableStateFlow<List<Lesson>>(emptyList())
  open val currentUserLessons: StateFlow<List<Lesson>> = _currentUserLessons.asStateFlow()

  private val _cancelledLessons = MutableStateFlow<List<Lesson>>(emptyList())
  open val cancelledLessons: StateFlow<List<Lesson>> = _cancelledLessons.asStateFlow()

  private val _selectedLesson = MutableStateFlow<Lesson?>(null)
  open val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

  init {
    repository.init {}
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
   * Generates a new unique ID.
   *
   * @return A new unique ID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /**
   * Adds a new lesson to the repository.
   *
   * @param lesson The Lesson object to be added.
   * @param onComplete Callback to execute when the operation completes.
   */
  fun addLesson(lesson: Lesson, onComplete: () -> Unit) {
    repository.addLesson(
        lesson = lesson,
        onSuccess = {
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error adding lesson: $lesson", it)
          onComplete() // Call the callback even if there's a failure
        })
  }

  /**
   * Updates an existing lesson in the repository.
   *
   * @param lesson The Lesson object to be updated.
   * @param onComplete Callback to execute when the operation completes.
   */
  fun updateLesson(lesson: Lesson, onComplete: () -> Unit) {
    repository.updateLesson(
        lesson = lesson,
        onSuccess = {
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error updating lesson: $lesson", it)
          onComplete() // Call the callback even if there's a failure
        })
  }

  /**
   * Deletes a lesson from the repository.
   *
   * @param lessonId The ID of the lesson to be deleted.
   * @param onComplete Callback to execute when the operation completes.
   */
  fun deleteLesson(lessonId: String, onComplete: () -> Unit) {
    repository.deleteLesson(
        lessonId = lessonId,
        onSuccess = {
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error deleting lesson: $lessonId", it)
          onComplete() // Call the callback even if there's a failure
        })
  }

  /**
   * Selects a lesson to be used in the view.
   *
   * @param lesson The Lesson object to be selected.
   */
  fun selectLesson(lesson: Lesson) {
    _selectedLesson.value = lesson
  }

  /** Un-Select the selected lesson. */
  fun unselectLesson() {
    _selectedLesson.value = null
  }

  /**
   * Fetches all lessons for a specific tutor.
   *
   * @param tutorUid The UID of the tutor.
   * @param onComplete Callback to execute when the operation completes.
   */
  fun getLessonsForTutor(tutorUid: String, onComplete: () -> Unit = {}) {
    repository.getLessonsForTutor(
        tutorUid = tutorUid,
        onSuccess = { fetchedLessons ->
          _currentUserLessons.value =
              fetchedLessons.filter {
                when (it.status) {
                  LessonStatus.STUDENT_REQUESTED -> it.tutorUid.contains(tutorUid)
                  LessonStatus.PENDING_TUTOR_CONFIRMATION,
                  LessonStatus.CONFIRMED,
                  LessonStatus.INSTANT_CONFIRMED,
                  LessonStatus.PENDING_REVIEW,
                  LessonStatus.COMPLETED -> true
                  else -> false
                }
              }
          _cancelledLessons.value =
              fetchedLessons.filter { lesson ->
                when (lesson.status) {
                  LessonStatus.STUDENT_CANCELLED -> true
                  LessonStatus.TUTOR_CANCELLED -> false
                  else -> false
                }
              }
          onComplete()
        },
        onFailure = {
          Log.e("LessonViewModel", "Error fetching tutor's lessons", it)
          onComplete()
        })
  }

  /**
   * Fetches all lessons for a specific student.
   *
   * @param studentUid The UID of the student.
   * @param onComplete Callback to execute when the operation completes.
   */
  fun getLessonsForStudent(studentUid: String, onComplete: () -> Unit = {}) {
    repository.getLessonsForStudent(
        studentUid = studentUid,
        onSuccess = { fetchedLessons ->
          _currentUserLessons.value = fetchedLessons
          _cancelledLessons.value =
              fetchedLessons.filter { lesson ->
                when (lesson.status) {
                  LessonStatus.STUDENT_CANCELLED -> false
                  LessonStatus.TUTOR_CANCELLED -> true
                  else -> false
                }
              }
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error fetching student's lessons", it)
          onComplete() // Call the callback even if there's a failure
        })
  }

  /**
   * Fetches all lessons with the specified status from the repository.
   *
   * @param onComplete Callback to execute when the operation completes.
   */
  fun getAllRequestedLessons(onComplete: () -> Unit = {}) {
    repository.getAllRequestedLessons(
        onSuccess = { fetchedLessons ->
          requestedLessons_.value = fetchedLessons
          onComplete()
        },
        onFailure = {
          Log.e("LessonViewModel", "Error fetching lessons by status", it)
          onComplete()
        })
  }
}
