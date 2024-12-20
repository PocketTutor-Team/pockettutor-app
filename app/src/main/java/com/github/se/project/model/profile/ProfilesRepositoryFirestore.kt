package com.github.se.project.model.profile

import android.util.Log
import androidx.core.net.toUri
import com.github.se.project.model.certification.EpflCertification
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

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

  override fun updateToken(
      uid: String,
      newToken: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val tokenUpdate = mapOf("token" to newToken)

    db.collection(collectionPath)
        .document(uid)
        .update(tokenUpdate)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
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
      val token = document.getString("token") ?: return null
      val googleUid = document.getString("googleUid") ?: return null
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      val phoneNumber = document.getString("phoneNumber") ?: return null
      val role = Role.valueOf(document.getString("role") ?: return null)
      val section = Section.valueOf(document.getString("section") ?: return null)
      val academicLevel = AcademicLevel.valueOf(document.getString("academicLevel") ?: return null)
      val description = document.getString("description") ?: ""
      val profilePhotoData = document.getString("profilePhotoData")?.toUri()
      val favoriteTutors = document.get("favoriteTutors")?.let { it as List<String> } ?: emptyList()

      val languages =
          document.get("languages")?.let { languagesList ->
            (languagesList as List<*>).mapNotNull {
              try {
                Language.valueOf(it.toString())
              } catch (e: IllegalArgumentException) {
                Log.e("ProfilesRepositoryFirestore", "Invalid language in document: $it", e)
                null
              }
            }
          } ?: emptyList()

      val subjects =
          document.get("subjects")?.let { subjectsList ->
            (subjectsList as List<*>).mapNotNull {
              try {
                Subject.valueOf(it.toString())
              } catch (e: IllegalArgumentException) {
                Log.e("ProfilesRepositoryFirestore", "Invalid subject in document: $it", e)
                null
              }
            }
          } ?: emptyList()

      val schedule =
          document.get("schedule")?.let { (it as List<Int>).chunked(12) }
              ?: List(7) { List(12) { 0 } }
      val price = document.getLong("price")?.toInt() ?: 0

      val certification =
          document.get("certification")?.let {
            val certificationMap = it as Map<*, *>
            val verificationDate = (certificationMap["verificationDate"] as Timestamp).toDate().time
            EpflCertification(
                sciper = certificationMap["sciper"] as String,
                verified = certificationMap["verified"] as Boolean,
                verificationDate = verificationDate)
          }

      Profile(
          uid,
          token,
          googleUid,
          firstName,
          lastName,
          phoneNumber,
          role,
          section,
          academicLevel,
          favoriteTutors,
          description,
          languages,
          subjects,
          schedule,
          price,
          certification,
          profilePhotoData)
    } catch (e: Exception) {
      Log.e("ProfilesRepositoryFirestore", "Error converting document to Profile", e)
      null
    }
  }

  private fun profileToMap(profile: Profile): Map<String, Any> {
    val baseMap =
        mutableMapOf(
            "uid" to profile.uid,
            "token" to profile.token,
            "googleUid" to profile.googleUid,
            "firstName" to profile.firstName,
            "lastName" to profile.lastName,
            "phoneNumber" to profile.phoneNumber,
            "role" to profile.role.name,
            "section" to profile.section.name,
            "academicLevel" to profile.academicLevel.name,
            "favoriteTutors" to profile.favoriteTutors,
            "description" to profile.description,
            "languages" to profile.languages.map { it.toString() },
            "subjects" to profile.subjects.map { it.toString() },
            "schedule" to profile.schedule.flatten(),
            "price" to profile.price)

    profile.profilePhotoUrl?.let { photoData -> baseMap["profilePhotoData"] = photoData }

    profile.certification?.let { cert ->
      baseMap["certification"] =
          mapOf(
              "sciper" to cert.sciper,
              "verified" to cert.verified,
              "verificationDate" to Timestamp(cert.verificationDate / 1000, 0))
    }

    return baseMap
  }
}
