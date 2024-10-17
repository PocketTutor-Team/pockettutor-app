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
class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

  private val currentUserLessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  val currentUserLessons: StateFlow<List<Lesson>> = currentUserLessons_.asStateFlow()

    private val selectedLesson_ = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = selectedLesson_.asStateFlow()

  init {
    repository.init {
      // Uncomment this if needed in the future to automatically load lessons, but this seems to
      // make the CI fails.
      // getAllLessons()
    }
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
    selectedLesson_.value = lesson
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
          currentUserLessons_.value = fetchedLessons
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error fetching tutor's lessons", it)
          onComplete() // Call the callback even if there's a failure
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
          currentUserLessons_.value = fetchedLessons
          onComplete() // Call the provided callback on success
        },
        onFailure = {
          Log.e("LessonViewModel", "Error fetching student's lessons", it)
          onComplete() // Call the callback even if there's a failure
        })
  }
}
