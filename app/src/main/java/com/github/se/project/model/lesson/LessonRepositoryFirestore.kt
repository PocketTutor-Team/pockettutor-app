package com.android.sample.model.lesson

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LessonRepositoryFirestore(private val db: FirebaseFirestore) : LessonRepository {

    private val lessonsCollectionPath = "lessons"

    // Initialize the repository
    override fun init(onSuccess: () -> Unit) {
        // Additional initialization logic if required
        onSuccess()
    }

    // Retrieve all lessons for a specific user by userUid
    override fun getLessonsByUserId(
        userUid: String,
        onSuccess: (List<Lesson>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(lessonsCollectionPath)
            .whereEqualTo("userUid", userUid)  // Filter lessons by user ID
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lessons =
                        task.result?.mapNotNull { document -> documentToLesson(document) } ?: emptyList()
                    onSuccess(lessons)
                } else {
                    task.exception?.let { e ->
                        Log.e("LessonRepositoryFirestore", "Error getting lessons for user", e)
                        onFailure(e)
                    }
                }
            }
    }

    // Add a new lesson for a specific user
    override fun addLessonByUserId(
        userUid: String,
        lesson: Lesson,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val task =
            db.collection(lessonsCollectionPath)
                .document(lesson.id)
                .set(lesson)

        performFirestoreOperation(task, onSuccess, onFailure)
    }

    // Update an existing lesson for a specific user
    override fun updateLessonByUserId(
        userUid: String,
        lesson: Lesson,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val task =
            db.collection(lessonsCollectionPath)
                .document(lesson.id)
                .set(lesson)

        performFirestoreOperation(task, onSuccess, onFailure)
    }

    // Delete a lesson by its ID for a specific user
    override fun deleteLessonByUserId(
        userUid: String,
        lessonId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // No need to filter by userUid because we're using the lesson ID to delete
        val task =
            db.collection(lessonsCollectionPath)
                .document(lessonId)
                .delete()

        performFirestoreOperation(task, onSuccess, onFailure)
    }

    /*
     * Performs a Firestore operation and calls the appropriate callback based on the result.
     *
     * @param task The Firestore task to perform.
     * @param onSuccess The callback to call if the operation is successful.
     * @param onFailure The callback to call if the operation fails.
     */
    private fun performFirestoreOperation(
        task: Task<Void>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        task.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                onSuccess()
            } else {
                result.exception?.let { e ->
                    Log.e("LessonRepositoryFirestore", "Error performing Firestore operation", e)
                    onFailure(e)
                }
            }
        }
    }

    /**
     * Converts a Firestore document to a Lesson object.
     *
     * @param document The Firestore document to convert.
     * @return The Lesson object, or null if the document could not be converted.
     */
    private fun documentToLesson(document: DocumentSnapshot): Lesson? {
        return try {
            val id = document.id
            val title = document.getString("title") ?: return null
            val description = document.getString("description") ?: return null
            val tutorUid = document.getString("tutorUid") ?: return null
            val studentUid = document.getString("studentUid") ?: return null
            val price = document.getDouble("price") ?: return null
            val timeSlot = document.getString("timeSlot") ?: return null
            val status = document.getString("status")?.let { LessonStatus.valueOf(it) } ?: return null
            val language = document.getString("language") ?: return null

            Lesson(id, title, description, tutorUid, studentUid, price, timeSlot, status, language)
        } catch (e: Exception) {
            Log.e("LessonRepositoryFirestore", "Error converting document to Lesson", e)
            null
        }
    }
}
