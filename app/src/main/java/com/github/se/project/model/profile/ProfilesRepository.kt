package com.github.se.project.model.profile

interface ProfilesRepository {
  fun getNewUid(): String

  fun init(onSuccess: () -> Unit)

  fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit)

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteProfileById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateToken(uid: String, newToken: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
