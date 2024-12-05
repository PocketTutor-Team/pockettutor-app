package com.github.se.project.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class ListProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

  // List of all profiles registered in the app.
  private val profiles_ = MutableStateFlow<List<Profile>>(emptyList())
  val profiles: StateFlow<List<Profile>> = profiles_.asStateFlow()
  // Note: This might be useful when a user log-in and already have an account.
  //  It might be deleted if it is not necessary.

  // The profile of the current user of the app (after log-in).
  private val currentProfile_ = MutableStateFlow<Profile?>(null)
  open val currentProfile: StateFlow<Profile?> = currentProfile_.asStateFlow()

  // This field is used to select another profile than the current user profile
  // (used for example for the SelectedTutorDetails screen to select the tutor profile to display)
  private val selectedProfile_ = MutableStateFlow<Profile?>(null)
  open val selectedProfile: StateFlow<Profile?> = selectedProfile_.asStateFlow()

  init {
    repository.init { getProfiles() }
  }

  // Create a factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ListProfilesViewModel(ProfilesRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  /**
   * Generates a new unique ID.
   *
   * @return A new unique ID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /** Gets all Profile documents and update the 'profiles' state flow. */
  fun getProfiles() {
    repository.getProfiles(
        onSuccess = { profiles_.value = it },
        onFailure = { Log.e("ListProfilesViewModel", "Error getting profiles", it) })
  }

  /**
   * Gets a Profile document by its ID.
   *
   * @param id The ID of the Profile document to be retrieved.
   * @return The Profile document with the given ID, or null if not found.
   */
  fun getProfileById(id: String): Profile? {
    getProfiles()
    return profiles_.value.find { it.uid == id }
  }

  fun getAveragePrice(): Double {
    getProfiles()
    val tutorProfiles = profiles_.value.filter { it.role == Role.TUTOR && it.price != 0 }
    return if (tutorProfiles.isEmpty()) 30.0 else tutorProfiles.map { it.price }.average()
  }

  /**
   * Adds a Profile document.
   *
   * @param profile The Profile document to be added.
   */
  fun addProfile(profile: Profile) {
    repository.addProfile(
        profile,
        onSuccess = { getProfiles() },
        onFailure = { Log.e("ListProfilesViewModel", "Error adding profile", it) })
  }

  /**
   * Updates a Profile document.
   *
   * @param profile The Profile document to be updated.
   */
  fun updateProfile(profile: Profile) {
    repository.updateProfile(
        profile,
        onSuccess = { getProfiles() },
        onFailure = { Log.e("ListProfilesViewModel", "Error updating profile", it) })
  }

  /**
   * Deletes a Profile document by its ID.
   *
   * @param id The ID of the Profile document to be deleted.
   */
  fun deleteProfileById(id: String) {
    repository.deleteProfileById(
        id,
        onSuccess = { getProfiles() },
        onFailure = { Log.e("ListProfilesViewModel", "Error deleting profile", it) })
  }

  /**
   * Selects a Profile document as the current user profile.
   *
   * @param profile The Profile document to be selected.
   */
  fun setCurrentProfile(profile: Profile?) {
    currentProfile_.value = profile
  }

  /**
   * Selects a Profile document
   *
   * @param profile the profile to select
   */
  fun selectProfile(profile: Profile) {
    selectedProfile_.value = profile
  }
}
