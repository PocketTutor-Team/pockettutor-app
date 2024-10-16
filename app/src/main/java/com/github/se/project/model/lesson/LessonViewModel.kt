package com.github.se.project.model.lesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.lesson.Lesson
import com.github.se.project.model.profile.ListProfilesViewModel
import com.github.se.project.model.profile.Role
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
class LessonsViewModel(
    private val repository: LessonRepository,
    profileViewModel: ListProfilesViewModel
) : ViewModel() {
  private val lessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  val lessons: StateFlow<List<Lesson>> = lessons_.asStateFlow()

  private val selectedLesson_ = MutableStateFlow<Lesson?>(null)
  val selectedLesson: StateFlow<Lesson?> = selectedLesson_.asStateFlow()

  private val isTutor: Boolean

  init {
    // Initialize isTutor based on the profile role
    val profile = profileViewModel.currentProfile.value
    isTutor = profile?.role == Role.TUTOR

    // Fetch lessons based on whether the user is a tutor or a student
    profile?.let {
      if (isTutor) {
        getLessonsByTutor(Firebase.auth.currentUser?.uid ?: "")
      } else {
        getLessonsByStudent(Firebase.auth.currentUser?.uid ?: "")
      }
    }
  }

  /** Factory for creating a LessonsViewModel. */
  companion object {
    fun Factory(profileViewModel: ListProfilesViewModel): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = LessonRepositoryFirestore(Firebase.firestore)
            return LessonsViewModel(repository, profileViewModel) as T
          }
        }
  }

  /**
   * Retrieves all lessons associated with a tutor.
   *
   * @param tutorUid The UID of the tutor whose lessons are to be retrieved.
   */
  fun getLessonsByTutor(tutorUid: String) {
    repository.getLessonsByTutorUid(
        tutorUid,
        onSuccess = { lessons_.value = it },
        onFailure = { e -> Log.e("LessonViewModel", "Error loading tutor's lessons", e) })
  }

  /**
   * Retrieves all lessons associated with a student.
   *
   * @param studentUid The UID of the student whose lessons are to be retrieved.
   */
  fun getLessonsByStudent(studentUid: String) {
    repository.getLessonsByStudentUid(
        studentUid,
        onSuccess = { lessons_.value = it },
        onFailure = { e -> Log.e("LessonViewModel", "Error loading student's lessons", e) })
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
        onSuccess = { getLessonsForUser(userUid) }, // Refresh the lesson list on success
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
        onSuccess = { getLessonsForUser(userUid) }, // Refresh the lesson list on success
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

  /** Fetches lessons based on whether the current user is a tutor or a student. */
  private fun getLessonsForUser(userUid: String) {
    if (isTutor) {
      getLessonsByTutor(userUid)
    } else {
      getLessonsByStudent(userUid)
    }
  }
}
