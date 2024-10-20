package com.github.se.project.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.EnumSet

class ProfilesRepositoryFirestore(private val db: FirebaseFirestore) : ProfilesRepository {

  private val collectionPath = "profiles"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    FirebaseAuth.getInstance().addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("ProfilesRepositoryFirestore", "getProfiles")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val profiles =
            task.result?.mapNotNull { document -> documentToProfile(document) } ?: emptyList()
        onSuccess(profiles)
      } else {
        task.exception?.let { e ->
          Log.e("ProfilesRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val profileAsMap = profileToMap(profile)
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profileAsMap), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val profileAsMap = profileToMap(profile)
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profileAsMap), onSuccess, onFailure)
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
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
          Log.e("ProfilesRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  /**
   * Converts a Firestore document to a Profile object.
   *
   * @param document The Firestore document to convert.
   * @return The Profile object, or null if the document could not be converted.
   */
  private fun documentToProfile(document: DocumentSnapshot): Profile? {
    return try {
      val uid = document.id
      val googleUid = document.getString("googleUid") ?: return null
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val phoneNumber = document.getString("phoneNumber") ?: return null
      val role = Role.valueOf(document.getString("role") ?: return null)
      val section = Section.valueOf(document.getString("section") ?: return null)
      val academicLevel = AcademicLevel.valueOf(document.getString("academicLevel") ?: return null)

      // Retrieve the "languages" field as a list of strings and map it to EnumSet<Language>
      // the type cast (as List<String>) is safe since in a try-catch block
      val languages =
          document
              .get("languages")
              ?.let { languagesList ->
                (languagesList as List<String>).map { Language.valueOf(it) }
              }
              ?.toCollection(EnumSet.noneOf(Language::class.java))
              ?: EnumSet.noneOf(Language::class.java)

      val subjects =
          document
              .get("subjects")
              ?.let { subjectsList -> (subjectsList as List<String>).map { Subject.valueOf(it) } }
              ?.toCollection(EnumSet.noneOf(Subject::class.java))
              ?: EnumSet.noneOf(Subject::class.java)

      val schedule =
          document.get("schedule")?.let { (it as List<Int>).chunked(12) }
              ?: List(7) { List(12) { 0 } }
      val price = document.getLong("price")?.toInt() ?: 0

      Profile(
          uid,
          googleUid,
          firstName,
          lastName,
          phoneNumber,
          role,
          section,
          academicLevel,
          languages,
          subjects,
          schedule,
          price)
    } catch (e: Exception) {
      Log.e("ProfilesRepositoryFirestore", "Error converting document to Profile", e)
      null
    }
  }

  /** Converts a Profile object to a Map for Firestore. */
  private fun profileToMap(profile: Profile): Map<String, Any> {
    return mapOf(
        "uid" to profile.uid,
        "googleUid" to profile.googleUid,
        "firstName" to profile.firstName,
        "lastName" to profile.lastName,
        "phoneNumber" to profile.phoneNumber,
        "role" to profile.role.name,
        "section" to profile.section.name,
        "academicLevel" to profile.academicLevel.name,
        "languages" to profile.languages.map { it.name }, // convert enumSets into a list of strings
        "subjects" to profile.subjects.map { it.name },
        "schedule" to profile.schedule.flatten(),
        "price" to profile.price)
  }
}
