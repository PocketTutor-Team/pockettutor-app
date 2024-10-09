package com.android.sample.model.lesson

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LessonRepositoryFirestore(private val db: FirebaseFirestore) : LessonRepository {

  private val collectionPath = "lessons"

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
    db.collection("users")
        .document(userUid)
        .collection(collectionPath)
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
    db.collection("users")
        .document(userUid)
        .collection(collectionPath)
        .document(lesson.id)
        .set(lesson)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error adding lesson for user", e)
              onFailure(e)
            }
          }
        }
  }

  // Update an existing lesson for a specific user
  override fun updateLessonByUserId(
      userUid: String,
      lesson: Lesson,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("users")
        .document(userUid)
        .collection(collectionPath)
        .document(lesson.id)
        .set(lesson)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error updating lesson for user", e)
              onFailure(e)
            }
          }
        }
  }

  // Delete a lesson by its ID for a specific user
  override fun deleteLessonByUserId(
      userUid: String,
      lessonId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("users")
        .document(userUid)
        .collection(collectionPath)
        .document(lessonId)
        .delete()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error deleting lesson for user", e)
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
      val date = document.getString("date") ?: return null

      Lesson(id, title, description, date)
    } catch (e: Exception) {
      Log.e("LessonRepositoryFirestore", "Error converting document to Lesson", e)
      null
    }
  }
}
