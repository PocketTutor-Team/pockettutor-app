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
    private val profileViewModel: ListProfilesViewModel
) : ViewModel() {

  private val allLessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  val allLessons: StateFlow<List<Lesson>> = allLessons_.asStateFlow()

  private val userLessons_ = MutableStateFlow<List<Lesson>>(emptyList())
  val userLessons: StateFlow<List<Lesson>> = userLessons_.asStateFlow()

  private val selectedLesson_ = MutableStateFlow<Lesson?>(null)
  val selectedLesson: StateFlow<Lesson?> = selectedLesson_.asStateFlow()

  private val currentUserId: String? = Firebase.auth.currentUser?.uid

  init {
    repository.init { fetchAllLessons() }
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

  // Filter lessons based on the current user's ID (tutor or student)
  private fun filterLessonsForCurrentUser(lessons: List<Lesson>) {
    val profile = profileViewModel.currentProfile.value
    profile?.let {
      userLessons_.value =
          if (it.role == Role.TUTOR) {
            lessons.filter { lesson -> lesson.tutorUid == currentUserId }
          } else {
            lessons.filter { lesson -> lesson.studentUid == currentUserId }
          }
    }
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
        onSuccess = { fetchAllLessons() }, // Refresh the lesson list on success
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
        onSuccess = { fetchAllLessons() }, // Refresh the lesson list on success
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

  // Fetch all lessons and then filter based on the current user
  fun fetchAllLessons() {
    repository.getAllLessons(
        onSuccess = { lessons ->
          allLessons_.value = lessons
          filterLessonsForCurrentUser(lessons)
        },
        onFailure = { e -> Log.e("LessonsViewModel", "Error loading all lessons", e) })
  }
}
