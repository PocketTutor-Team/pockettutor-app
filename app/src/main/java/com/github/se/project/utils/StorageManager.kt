package com.github.se.project.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlinx.coroutines.tasks.await

object StorageManager {
  private const val PROFILE_PHOTOS_PATH = "profile_photos"
  private val storage = FirebaseStorage.getInstance()
  private val storageRef = storage.reference

  // Maximum file size: 5MB
  private const val MAX_FILE_SIZE = 5 * 1024 * 1024

  suspend fun uploadProfilePhoto(imageUri: Uri, userId: String, context: Context): Uri? {
    return try {
      validateImageSizeAndType(imageUri, context)

      // Delete old photos first
      deleteOldPhotos(userId)

      // Upload new photo
      val fileName = "$PROFILE_PHOTOS_PATH/${generateFileName(userId)}"
      val photoRef = storageRef.child(fileName)

      photoRef.putFile(imageUri).await()
      val downloadUri = photoRef.downloadUrl.await()
      Log.d("StorageManager", "Photo uploaded successfully: $downloadUri")
      downloadUri
    } catch (e: Exception) {
      Log.e("StorageManager", "Error uploading image", e)
      null
    }
  }

  suspend fun deleteOldPhotos(userId: String) {
    try {
      val oldPhotos =
          storageRef.child(PROFILE_PHOTOS_PATH).listAll().await().items.filter {
            it.name.startsWith(userId)
          }

      oldPhotos.forEach { photo ->
        try {
          photo.delete().await()
          Log.d("StorageManager", "Deleted old photo: ${photo.name}")
        } catch (e: Exception) {
          Log.w("StorageManager", "Failed to delete old photo: ${photo.name}", e)
        }
      }
    } catch (e: Exception) {
      Log.e("StorageManager", "Error cleaning old photos", e)
    }
  }

  private fun validateImageSizeAndType(imageUri: Uri, context: Context) {
    val fileSize = context.contentResolver.openInputStream(imageUri)?.use { it.available() } ?: 0
    if (fileSize > MAX_FILE_SIZE) {
      throw IllegalArgumentException("File size exceeds 2MB limit")
    }

    val mimeType = context.contentResolver.getType(imageUri)
    if (!isValidImageType(mimeType)) {
      throw IllegalArgumentException("Invalid file type. Please upload a JPG or PNG image")
    }
  }

  private fun generateFileName(userId: String): String {
    return "${userId}_${UUID.randomUUID()}.jpg"
  }

  private fun isValidImageType(mimeType: String?): Boolean {
    return mimeType in listOf("image/jpeg", "image/jpg", "image/png")
  }
}
