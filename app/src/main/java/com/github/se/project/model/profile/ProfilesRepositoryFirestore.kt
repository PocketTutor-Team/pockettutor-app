package com.github.se.project.model.profile

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfilesRepositoryFirestore(private val db: FirebaseFirestore) : ProfilesRepository {

  private val collectionPath = "profiles"

  override fun getNewUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
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
    val uploadProfile = profileUpload(profile)
    val task: Task<Void> =
        db.collection(collectionPath).document(uploadProfile.uid).set(uploadProfile)
    performFirestoreOperation(task, onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val uploadProfile = profileUpload(profile)
    performFirestoreOperation(
        db.collection(collectionPath).document(uploadProfile.uid).set(uploadProfile),
        onSuccess,
        onFailure)
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
    println("we also here")
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        println("success")
        onSuccess()
      } else {
        println("failure")
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
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val phoneNumber = document.getString("phoneNumber") ?: return null
      val role = document.getString("role") ?: return null
      val section = document.getString("section") ?: return null
      val academicLevel = document.getString("academicLevel") ?: return null
      // Note that in the above section, we could do "Section.valueOf([current expression]).NAME",
      // to check the string corresponds to the enums, at the cost of making the code less legible
      val uploadProfile =
          ProfileUpload(uid, firstName, lastName, phoneNumber, role, section, academicLevel)
      profileFromUpload(uploadProfile)
    } catch (e: Exception) {
      Log.e("ProfilesRepositoryFirestore", "Error converting document to Profile", e)
      null
    }
  }
}
