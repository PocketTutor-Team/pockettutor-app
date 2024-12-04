package com.github.se.project.model.certification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.project.model.profile.ListProfilesViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the EPFL profile certification process. Handles the
 * verification workflow and updates the user's profile with verified information.
 */
class CertificationViewModel(
    private val repository: EpflVerificationRepository,
    private val profilesViewModel: ListProfilesViewModel
) : ViewModel() {

  // Represents the current state of the verification process
  private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
  val verificationState: StateFlow<VerificationState> = _verificationState.asStateFlow()

  // Track attempts to prevent brute force attempts
  private var verificationAttempts = 0
  private val maxAttempts = 3

  /**
   * Sealed class representing all possible states during the verification process. This helps
   * maintain type safety and provides a clear contract for UI states.
   */
  sealed class VerificationState {
    object Idle : VerificationState()

    object Loading : VerificationState()

    data class Success(val result: VerificationResult.Success) : VerificationState()

    data class Error(val error: VerificationResult.Error, val message: String) :
        VerificationState()

    object TooManyAttempts : VerificationState()
  }

  /** Factory for creating CertificationViewModel instances with dependencies */
  companion object {
    fun Factory(
        repository: EpflVerificationRepository,
        profilesViewModel: ListProfilesViewModel
    ): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CertificationViewModel(repository, profilesViewModel) as T
          }
        }
  }

  /**
   * Initiates the SCIPER verification process. This method validates the input, tracks attempts,
   * and manages the verification flow.
   */
  fun verifySciperNumber(sciper: String) {
    // Check if we've exceeded the maximum attempts
    if (verificationAttempts >= maxAttempts) {
      _verificationState.value = VerificationState.TooManyAttempts
      return
    }

    // Increment attempt counter
    verificationAttempts++

    viewModelScope.launch {
      try {
        _verificationState.value = VerificationState.Loading

        // Perform the verification
        when (val result = repository.verifySciper(sciper)) {
          is VerificationResult.Success -> {
            _verificationState.value = VerificationState.Success(result)
            updateProfile(result, sciper)
          }
          is VerificationResult.Error -> {
            val message =
                when (result) {
                  is VerificationResult.Error.InvalidSciper ->
                      "Invalid SCIPER format. Please enter a 6-digit number."
                  is VerificationResult.Error.NetworkError ->
                      "Network error. Please check your connection and try again."
                  is VerificationResult.Error.ParsingError ->
                      "Unable to verify EPFL profile. Please try again later."
                }
            _verificationState.value = VerificationState.Error(result, message)
          }
        }
      } catch (e: Exception) {
        Log.e("CertificationViewModel", "Error during verification", e)
        _verificationState.value =
            VerificationState.Error(
                VerificationResult.Error.NetworkError,
                "An unexpected error occurred. Please try again.")
      }
    }
  }

  /**
   * Updates the user's profile with verified EPFL information. This method is called after
   * successful verification to persist the changes.
   */
  private fun updateProfile(result: VerificationResult.Success, sciper: String) {
    val currentProfile =
        profilesViewModel.currentProfile.value
            ?: run {
              Log.e("CertificationViewModel", "No current profile found")
              return
            }

    try {
      // Create new certification status
      val certification = EpflCertification(sciper = sciper, verified = true)

      // Update profile with verified information
      val updatedProfile =
          currentProfile.copy(
              firstName = result.firstName,
              lastName = result.lastName,
              academicLevel = result.academicLevel,
              section = result.section,
              certification = certification)

      // Persist the changes
      profilesViewModel.updateProfile(updatedProfile)
    } catch (e: Exception) {
      Log.e("CertificationViewModel", "Error updating profile", e)
      _verificationState.value =
          VerificationState.Error(
              VerificationResult.Error.ParsingError,
              "Failed to update profile with verified information.")
    }
  }

  /**
   * Resets the verification state and attempt counter. This can be used when navigating away or
   * wanting to start fresh.
   */
  fun resetVerification() {
    verificationAttempts = 0
    _verificationState.value = VerificationState.Idle
  }

  /** Checks if the profile is already verified with EPFL credentials */
  fun isProfileVerified(): Boolean {
    return profilesViewModel.currentProfile.value?.certification?.verified == true
  }
}
