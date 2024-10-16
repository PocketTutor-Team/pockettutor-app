package com.android.sample.model.lesson

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

  // Retrieve all lessons for a specific user by userUid
  override fun getLessonsByUserId(
      userUid: String,
      onSuccess: (List<Lesson>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereEqualTo("studentUid", userUid) // Filter lessons by user ID
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

  // Retrieve all lessons for a specific user by userUid
  override fun getLessons(onSuccess: (List<Lesson>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("LessonRepositoryFirestore", "getLessons")
    db.collection(collectionPath)
        // .whereEqualTo("studentUid", userUid)
        .get()
        // Log.d its content
        .addOnSuccessListener { querySnapshot ->
          // Log the raw document content before processing
          querySnapshot.documents.forEach { document ->
            Log.d("After the get", "Document Data: ${document.data}")
          }
        }
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            task.result?.let { querySnapshot ->
              // Create a mutable list to store lessons
              val lessons = mutableListOf<Lesson>()

              // Iterate over documents and convert them to Lesson objects
              querySnapshot.documents.forEach { document ->
                val lesson = documentToLesson(document)
                lesson?.let {
                  lessons.add(it) // Add only non-null lessons
                } ?: Log.d("LessonConversion", "Failed to convert document: ${document.id}")
              }
              // Call onSuccess with the filled lessons list
              onSuccess(lessons)
            }
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
    val lessonAsMap = lessonToMap(lesson)
    val task = db.collection(collectionPath).document(lesson.id).set(lessonAsMap)

    performFirestoreOperation(task, onSuccess, onFailure)
  }

  // Update an existing lesson for a specific user
  override fun updateLessonByUserId(
      userUid: String,
      lesson: Lesson,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val lessonAsMap = lessonToMap(lesson)
    val task = db.collection(collectionPath).document(lesson.id).set(lessonAsMap)

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
    val task = db.collection(collectionPath).document(lessonId).delete()

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
      val status = LessonStatus.valueOf(document.getString("status") ?: return null)
      val language = document.getString("language") ?: return null
      val lesson =
          Lesson(id, title, description, tutorUid, studentUid, price, timeSlot, status, language)
      Log.d("documentToLesson", lesson.title)
      return lesson
    } catch (e: Exception) {
      Log.e("LessonRepositoryFirestore", "Error converting document to Lesson", e)
      null
    }
  }

  /** Converts a Lesson object to a Map for Firestore. */
  private fun lessonToMap(lesson: Lesson): Map<String, Any> {
    return mapOf(
        "id" to lesson.id,
        "title" to lesson.title,
        "description" to lesson.description,
        "tutorUid" to lesson.tutorUid,
        "studentUid" to lesson.studentUid,
        "price" to lesson.price,
        "timeSlot" to lesson.timeSlot,
        "status" to lesson.status.name,
        "language" to lesson.language)
  }
}
