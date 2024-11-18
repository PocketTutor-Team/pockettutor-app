package com.github.se.project.model.lesson

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.github.se.project.model.profile.Language
import com.github.se.project.model.profile.Subject
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

class LessonRepositoryFirestore(private val db: FirebaseFirestore) : LessonRepository {

  private val collectionPath = "lessons"

  // Method to get a new unique identifier
  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  // Initialize the repository
  override fun init(onSuccess: () -> Unit) {
    FirebaseAuth.getInstance().addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getAllRequestedLessons(
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
      val filter = Filter.inArray("status", listOf(LessonStatus.STUDENT_REQUESTED.name, LessonStatus.INSTANT_REQUESTED.name))
    db.collection(collectionPath)
        .where(filter)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val lessons =
                task.result?.documents?.mapNotNull { document -> documentToLesson(document) }
                    ?: emptyList()
            onSuccess(lessons)
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error getting requested lessons", e)
              onFailure(e)
            }
          }
        }
  }

  // General method to retrieve lessons based on a user field (tutor or student)
  override fun getLessonsForStudent(
      studentUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereEqualTo(
            "studentUid", studentUid) // Filter lessons by user field (tutorUid or studentUid)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val lessons =
                task.result?.mapNotNull { document -> documentToLesson(document) } ?: emptyList()
            onSuccess(lessons)
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error getting lessons", e)
              onFailure(e)
            }
          }
        }
  }

  // Retrieve all lessons for a specific tutor
  override fun getLessonsForTutor(
      tutorUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereArrayContains("tutorUid", tutorUid)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val lessons =
                task.result?.mapNotNull { document -> documentToLesson(document) } ?: emptyList()
            onSuccess(lessons)
          } else {
            task.exception?.let { e ->
              Log.e("LessonRepositoryFirestore", "Error getting lessons", e)
              onFailure(e)
            }
          }
        }
  }

  // Add a new lesson
  override fun addLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val task = db.collection(collectionPath).document(lesson.id).set(lesson)
    performFirestoreOperation(task, onSuccess, onFailure)
  }

  // Update an existing lesson by its ID
  override fun updateLesson(lesson: Lesson, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val task = db.collection(collectionPath).document(lesson.id).set(lesson)
    performFirestoreOperation(task, onSuccess, onFailure)
  }

  // Delete a lesson by its ID
  override fun deleteLesson(
      lessonId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // No need to filter by userUid because we're using the lesson ID to delete
    val task = db.collection(collectionPath).document(lessonId).delete()
    performFirestoreOperation(task, onSuccess, onFailure)
  }

  /**
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
  @VisibleForTesting
  internal fun documentToLesson(document: DocumentSnapshot): Lesson? {
    return try {
      val id = document.id
      val title = document.getString("title") ?: return null
      val description = document.getString("description") ?: return null
      val subject = document.getString("subject")?.let { Subject.valueOf(it) } ?: return null

      val studentUid = document.getString("studentUid") ?: return null
      val minPrice = document.getDouble("minPrice") ?: return null
      val maxPrice = document.getDouble("maxPrice") ?: return null
      val price = document.getDouble("price") ?: return null
      val timeSlot = document.getString("timeSlot") ?: return null
      val status = LessonStatus.valueOf(document.getString("status") ?: return null)
      val latitude = document.getDouble("latitude") ?: return null
      val longitude = document.getDouble("longitude") ?: return null

      val tutorUid =
          document.get("tutorUid")?.let { tutorUid ->
            (tutorUid as List<*>).mapNotNull {
              try {
                it.toString()
              } catch (e: IllegalArgumentException) {
                Log.e("LessonRepositoryFirestore", "Invalid tutorId in document: $it", e)
                null
              }
            }
          } ?: emptyList()

      val language =
          document.get("languages")?.let { languagesList ->
            (languagesList as List<*>).mapNotNull {
              try {
                Language.valueOf(it.toString())
              } catch (e: IllegalArgumentException) {
                Log.e("LessonRepositoryFirestore", "Invalid language in document: $it", e)
                null
              }
            }
          } ?: emptyList()

      Lesson(
          id,
          title,
          description,
          subject,
          language,
          tutorUid,
          studentUid,
          minPrice,
          maxPrice,
          price,
          timeSlot,
          status,
          latitude,
          longitude)
    } catch (e: Exception) {
      Log.e("LessonRepositoryFirestore", "Error converting document to Lesson", e)
      null
    }
  }
}
