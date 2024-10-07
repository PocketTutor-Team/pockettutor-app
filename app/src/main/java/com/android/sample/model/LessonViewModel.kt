package com.android.sample.model.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.Lesson
import com.android.sample.LessonRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing lessons and interacting with the LessonRepository.
 * Handles the retrieval, addition, and deletion of lessons.
 *
 */
class LessonsViewModel(private val repository: LessonRepository) : ViewModel() {
    private val lessons_ = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = lessons_.asStateFlow()

    private val selectedLesson_ = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = selectedLesson_.asStateFlow()

    init {
        repository.init {
            // Initialization logic for the repository can be added here
        }
    }

    /**
     * Retrieves all lessons associated with a specific user.
     *
     * @param userUid The UID of the user whose lessons are to be retrieved.
     */
    fun getLessonsByUser(userUid: String) {
        repository.getLessonsByUser(
            userUid,
            onSuccess = { lessons_.value = it },
            onFailure = { /* Handle failure if needed */ }
        )
    }

    /**
     * Adds a new lesson to the repository.
     *
     * @param userUid The UID of the user to associate with the lesson.
     * @param lesson The Lesson object to be added.
     */
    fun addLesson(userUid: String, lesson: Lesson) {
        repository.addLesson(
            lesson = lesson,
            onSuccess = { getLessonsByUser(userUid) }, // Refresh the lesson list on success
            onFailure = { /* Handle failure if needed */ }
        )
    }

    /**
     * Deletes a lesson from the repository.
     *
     * @param userUid The UID of the user associated with the lesson.
     * @param lessonId The ID of the lesson to be deleted.
     */
    fun deleteLesson(userUid: String, lessonId: String) {
        repository.deleteLesson(
            lessonId = lessonId,
            onSuccess = { getLessonsByUser(userUid) }, // Refresh the lesson list on success
            onFailure = { /* Handle failure if needed */ }
        )
    }

    /**
     * Selects a lesson to be used in the view.
     *
     * @param lesson The Lesson object to be selected.
     */
    fun selectLesson(lesson: Lesson) {
        selectedLesson_.value = lesson
    }

    // Uncomment once LessonRepository is implemented as LessonRepositoryFirestore
    /*
    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LessonsViewModel(LessonRepositoryFirestore(Firebase.firestore)) as T
                }
            }
    }
    */
}
