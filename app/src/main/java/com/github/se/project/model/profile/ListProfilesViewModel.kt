package com.github.se.project.model.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing profiles in the application. It handles fetching, updating, and managing
 * the state of user profiles.
 */
open class ListProfilesViewModel(private val repository: ProfilesRepository) : ViewModel() {

  // Backing property to hold the list of all profiles in the app.
  private val profiles_ = MutableStateFlow<List<Profile>>(emptyList())
  // Exposed immutable StateFlow of profiles to be observed by the UI.
  val profiles: StateFlow<List<Profile>> = profiles_.asStateFlow()

  // Backing property for the current user's profile after login.
  private val currentProfile_ = MutableStateFlow<Profile?>(null)
  // Exposed immutable StateFlow for observing the current user profile.
  open val currentProfile: StateFlow<Profile?> = currentProfile_.asStateFlow()

  // Backing property for a selected profile (e.g., for displaying tutor details).
  private val selectedProfile_ = MutableStateFlow<Profile?>(null)
  // Exposed immutable StateFlow for observing the selected profile.
  open val selectedProfile: StateFlow<Profile?> = selectedProfile_.asStateFlow()

  init {
    // Initialize the repository and fetch profiles when ViewModel is created.
    repository.init { getProfiles() }
  }

  /**
   * Factory object for creating instances of ListProfilesViewModel with the appropriate repository.
   */
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
   * Generates a new unique identifier (UID).
   *
   * @return A string representing a new unique UID.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /** Fetches all profiles from the repository and updates the profiles StateFlow. */
  fun getProfiles() {
    repository.getProfiles(
        onSuccess = { profiles_.value = it }, // Update profiles on successful fetch.
        onFailure = { Log.e("ListProfilesViewModel", "Error getting profiles", it) } // Log errors.
        )
  }

  /**
   * Fetches a specific profile by its ID.
   *
   * @param id The unique ID of the profile to retrieve.
   * @return The Profile object with the matching ID, or null if not found.
   */
  fun getProfileById(id: String): Profile? {
    getProfiles() // Refresh the profiles list.
    return profiles_.value.find { it.uid == id }
  }

  fun getAveragePrice(): Double {
    getProfiles()
    val tutorProfiles = profiles_.value.filter { it.role == Role.TUTOR && it.price != 0 }
    return if (tutorProfiles.isEmpty()) 30.0 else tutorProfiles.map { it.price }.average()
  }

  /**
   * Adds a new profile to the repository and updates the profiles list.
   *
   * @param profile The Profile object to add.
   */
  fun addProfile(profile: Profile) {
    repository.addProfile(
        profile,
        onSuccess = { getProfiles() }, // Refresh the list on success.
        onFailure = { Log.e("ListProfilesViewModel", "Error adding profile", it) } // Log errors.
        )
  }

  /**
   * Updates an existing profile in the repository.
   *
   * @param profile The Profile object with updated data.
   */
  fun updateProfile(profile: Profile) {
    repository.updateProfile(
        profile,
        onSuccess = {
          getProfiles()
          val current = currentProfile_.value
          if (current != null && current.uid == profile.uid) {
            currentProfile_.value = profile
          }
        },
        onFailure = { Log.e("ListProfilesViewModel", "Error updating profile", it) } // Log errors.
        )
  }

  /**
   * Deletes a profile from the repository by its ID.
   *
   * @param id The unique ID of the profile to delete.
   */
  fun deleteProfileById(id: String) {
    repository.deleteProfileById(
        id,
        onSuccess = { getProfiles() }, // Refresh the list on success.
        onFailure = { Log.e("ListProfilesViewModel", "Error deleting profile", it) } // Log errors.
        )
  }

  /** Clears the current user's profile by setting it to null. */
  fun clearCurrentProfile() {
    currentProfile_.value = null
  }

  /**
   * Sets a specific profile as the current user's profile.
   *
   * @param profile The Profile object to set as the current profile.
   */
  fun setCurrentProfile(profile: Profile?) {
    currentProfile_.value = profile
  }

  /**
   * Selects a profile for viewing or other operations (not the current user profile).
   *
   * @param profile The Profile object to set as the selected profile.
   */
  fun selectProfile(profile: Profile) {
    selectedProfile_.value = profile
  }
}
