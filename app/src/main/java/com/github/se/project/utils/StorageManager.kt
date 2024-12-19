package com.github.se.project.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlinx.coroutines.tasks.await

object StorageManager {
  private const val PROFILE_PHOTOS_PATH = "profile_photos"
  private val storage = FirebaseStorage.getInstance()
  private val storageRef = storage.reference

  private const val MAX_FILE_SIZE = 5 * 1024 * 1024

  suspend fun uploadProfilePhoto(imageUri: Uri, userId: String, context: Context): Uri? {
    return try {
      if (FirebaseAuth.getInstance().currentUser == null) {
        Log.e("StorageManager", "User not authenticated")
        return null
      }

      validateImageSizeAndType(imageUri, context)

      deleteOldPhotos(userId)

      val fileName = "$PROFILE_PHOTOS_PATH/$userId/${UUID.randomUUID()}.jpg"
      val photoRef = storageRef.child(fileName)

      Log.d("StorageManager", "Starting upload for user $userId to path: $fileName")

      photoRef.putFile(imageUri).await()
      Log.d("StorageManager", "File uploaded successfully")

      val downloadUrl = photoRef.downloadUrl.await()
      Log.d("StorageManager", "Download URL obtained: $downloadUrl")

      downloadUrl
    } catch (e: Exception) {
      Log.e("StorageManager", "Error uploading image: ${e.message}", e)
      null
    }
  }

  suspend fun deleteOldPhotos(userId: String) {
    try {
      val path = "$PROFILE_PHOTOS_PATH/$userId"
      val folderRef = storageRef.child(path)

      val result = folderRef.listAll().await()
      result.items.forEach { item ->
        try {
          item.delete().await()
          Log.d("StorageManager", "Deleted: ${item.path}")
        } catch (e: Exception) {
          Log.w("StorageManager", "Failed to delete: ${item.path}", e)
        }
      }
    } catch (e: Exception) {
      Log.e("StorageManager", "Error listing/deleting old photos", e)
    }
  }

  private fun validateImageSizeAndType(imageUri: Uri, context: Context) {
    val fileSize = context.contentResolver.openInputStream(imageUri)?.use { it.available() } ?: 0
    if (fileSize > MAX_FILE_SIZE) {
      throw IllegalArgumentException("File size exceeds 5MB limit")
    }

    val mimeType = context.contentResolver.getType(imageUri)
    if (!isValidImageType(mimeType)) {
      throw IllegalArgumentException("Invalid file type. Please use JPG or PNG")
    }
  }

  private fun isValidImageType(mimeType: String?): Boolean {
    return mimeType in listOf("image/jpeg", "image/jpg", "image/png")
  }
}
